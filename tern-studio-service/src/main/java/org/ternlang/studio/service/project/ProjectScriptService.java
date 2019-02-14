package org.ternlang.studio.service.project;

import static org.ternlang.studio.common.resource.SessionConstants.SESSION_ID;
import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.studio.common.resource.display.DisplayPersister;
import org.ternlang.studio.project.BackupManager;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.service.ConnectListener;
import org.ternlang.studio.service.ProcessManager;
import org.ternlang.studio.service.agent.local.LocalProcessClient;
import org.ternlang.studio.service.command.CommandController;
import org.ternlang.studio.service.command.CommandListener;
import org.ternlang.studio.service.tree.TreeContextManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectScriptService implements Service {
   
   private final DisplayPersister displayPersister;
   private final TreeContextManager treeManager;
   private final ProjectProblemFinder problemFinder;
   private final ConnectListener connectListener;
   private final LocalProcessClient debugService;
   private final ProcessManager processManager;
   private final BackupManager backupManager;
   private final Workspace workspace;
   
   public ProjectScriptService(
         ProcessManager processManager, 
         ConnectListener connectListener, 
         Workspace workspace, 
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         DisplayPersister displayPersister,
         LocalProcessClient debugService,
         ThreadPool pool) 
   {
      this.problemFinder = new ProjectProblemFinder(workspace, pool);
      this.displayPersister = displayPersister;
      this.treeManager = treeManager;
      this.backupManager = backupManager;
      this.connectListener = connectListener;
      this.workspace = workspace;
      this.processManager = processManager;
      this.debugService = debugService;
   }  
  
   @Override
   public void connect(Session connection) {
      Request request = connection.getRequest();    
      Path path = request.getPath(); // /connect/<project-name>
      
      try {
         FrameChannel channel = connection.getChannel();
         Project project = workspace.createProject(path);
         Cookie cookie = request.getCookie(SESSION_ID);
         String value = null;
         
         if(cookie != null) {
            value = cookie.getValue();
         }
         try {
            CommandListener commandListener = new CommandListener(
                  processManager, 
                  problemFinder, 
                  displayPersister,
                  debugService,
                  channel, 
                  backupManager, 
                  treeManager,
                  project,
                  path, 
                  value);
            CommandController commandController = new CommandController(commandListener);

            channel.register(commandController);
            connectListener.connect(commandListener, path); // if there is a script then execute it
         } catch(Exception e) {
            log.info("Could not connect " + path, e);
         }
      }catch(Exception e){
         log.info("Error connecting " + path, e);
      }
      
   }
}