package org.ternlang.studio.service.project;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.springframework.stereotype.Component;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.studio.common.resource.ResourcePath;
import org.ternlang.studio.common.resource.SessionConstants;
import org.ternlang.studio.common.resource.display.DisplayPersister;
import org.ternlang.studio.project.BackupManager;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.service.ConnectListener;
import org.ternlang.studio.service.ProcessManager;
import org.ternlang.studio.service.StudioClientLauncher;
import org.ternlang.studio.service.agent.local.LocalProcessClient;
import org.ternlang.studio.service.command.CommandClient;
import org.ternlang.studio.service.command.CommandController;
import org.ternlang.studio.service.command.CommandListener;
import org.ternlang.studio.service.command.CommandSession;
import org.ternlang.studio.service.tree.TreeContextManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ResourcePath("/connect.*")
public class ProjectScriptService implements Service {

   private final ConcurrentMap<String, CommandSession> commandSessions;
   private final StudioClientLauncher clientLauncher;
   private final DisplayPersister displayPersister;
   private final TreeContextManager treeManager;
   private final ProjectProblemFinder problemFinder;
   private final ConnectListener connectListener;
   private final LocalProcessClient debugService;
   private final ProcessManager processManager;
   private final BackupManager backupManager;
   private final Workspace workspace;
   
   public ProjectScriptService(
         StudioClientLauncher clientLauncher,
         ProcessManager processManager, 
         ConnectListener connectListener, 
         Workspace workspace, 
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         DisplayPersister displayPersister,
         LocalProcessClient debugService,
         ThreadPool pool) 
   {
      this.commandSessions = new ConcurrentHashMap<String, CommandSession>();
      this.problemFinder = new ProjectProblemFinder(workspace, pool);
      this.displayPersister = displayPersister;
      this.treeManager = treeManager;
      this.backupManager = backupManager;
      this.connectListener = connectListener;
      this.clientLauncher = clientLauncher;
      this.workspace = workspace;
      this.processManager = processManager;
      this.debugService = debugService;
   }  
  
   @Override
   public void connect(Session connection) {
      Request request = connection.getRequest();
      Response response = connection.getResponse();
      Path path = request.getPath(); // /connect/<project-name>
      
      try {
         FrameChannel channel = connection.getChannel();
         Project project = workspace.createProject(path);
         String value = SessionConstants.findOrCreate(request, response);
         CommandSession commandSession = commandSessions.computeIfAbsent(value, CommandSession::new);
         CommandClient commandClient = commandSession.createClient(channel, project);

         try {
            CommandListener commandListener = new CommandListener(
                  clientLauncher,
                  processManager, 
                  problemFinder, 
                  displayPersister,
                  debugService,
                  backupManager, 
                  treeManager,
                  commandSession,
                  commandClient,
                  project,
                  path, 
                  value);
            CommandController commandController = new CommandController(commandListener);

            channel.register(commandController);
            connectListener.connect(commandListener, path); // if there is a script then execute it
         } catch(Exception e) {
            log.info("Could not connect " + path, e);
         }
      }catch(Exception e) {
         log.info("Error connecting " + path, e);
      }
   }
}