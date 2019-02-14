package org.ternlang.studio.service.project;

import static org.simpleframework.http.Protocol.LAST_MODIFIED;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.ternlang.core.Reserved;
import org.ternlang.studio.common.resource.Resource;
import org.ternlang.studio.common.resource.ResourcePath;
import org.ternlang.studio.project.BackupFile;
import org.ternlang.studio.project.BackupManager;
import org.ternlang.studio.project.FileSystem;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
@ResourcePath("/history.*")
public class ProjectHistoryResource implements Resource {

   private final BackupManager manager;
   private final Workspace workspace;
   private final Gson gson;
   
   public ProjectHistoryResource(Workspace workspace, BackupManager manager){
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.workspace = workspace;
      this.manager = manager;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      response.setStatus(Status.OK);

      try {
         if(!handleFindBackup(request, response)) {
            handleBackupHistory(request, response);
         }
      }catch(Exception e) {
         handleNotFound(request, response);
      }
   }
   
   private void handleBackupHistory(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2);
      Project project = workspace.createProject(path);
      FileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getName();
      long modificationTime = file.lastModified();
      List<BackupFile> files = manager.findAllBackups(file, name);
      Date modificationDate = new Date(modificationTime);
      BackupFile currentFile = new BackupFile(file, projectPath, modificationDate, "current", name);
      
      files.add(0, currentFile);
      PrintStream stream = response.getPrintStream();
      String text = gson.toJson(files);
      
      response.setContentType("application/json");
      stream.println(text);
      stream.close();
   }
   
   private boolean handleFindBackup(Request request, Response response) throws Throwable {
      String timeStamp = request.getParameter("time"); // do we load file
      
      if(timeStamp != null) {
         File backupFile = findBackupFile(request, response);
         OutputStream output = response.getOutputStream();
         long lastModified = backupFile.lastModified();
         
         response.setContentType("text/plain");
         response.setDate(LAST_MODIFIED, lastModified);
         
         InputStream source = new FileInputStream(backupFile);
         byte[] chunk = new byte[1024];
         int count = 0;
         
         while((count = source.read(chunk)) != -1){
            output.write(chunk, 0, count);
         }
         source.close();
         output.close();
         return true; // we found it
      }
      return false;
   }
   
   private File findBackupFile(Request request, Response response) throws Throwable {
      String timeStamp = request.getParameter("time"); // do we load file
      Path path = request.getPath(); 
      Project project = workspace.createProject(path);
      FileSystem system = project.getFileSystem();
      File file = system.getFile(path);
      String name = project.getName();
      List<BackupFile> files = manager.findAllBackups(file, name);
      
      for(BackupFile entry : files) {
         if(entry.getTimeStamp().equals(timeStamp)) {
            return entry.getFile();
         }
      }
      return file;
   }
   
   private void handleNotFound(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2);
      PrintStream out = response.getPrintStream();
      response.setStatus(Status.NOT_FOUND);
      response.setContentType("text/plain");
      
      if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
         out.println("// No source found for " + projectPath);
      }
      out.close();
   }
}