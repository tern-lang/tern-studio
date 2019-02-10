package tern.studio.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {

   private final Map<String, FileData> cache;
   private final Workspace workspace;
   
   public FileCache(Workspace workspace) {
      this.cache = new ConcurrentHashMap<String, FileData>();
      this.workspace = workspace;
   }
   
   public FileData getFile(String projectName, String projectPath) throws Exception {
      String pathKey = projectName + ":" + projectPath;
      FileData file = cache.get(pathKey);
      
      if(file == null || file.isStale()) {
         Project project = workspace.createProject(projectName);
         FileSystem fileSystem = project.getFileSystem();
         
         file = fileSystem.readFile(projectPath);
         cache.put(pathKey, file);
      }
      return file;
   }
   
}