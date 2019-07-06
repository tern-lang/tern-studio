package org.ternlang.studio.project;

import java.io.File;
import java.util.Arrays;

public class ProjectLayout {

   private final String[] paths;
   
   public ProjectLayout(String... paths) {
      this.paths = paths;
   }
   
   public String[] getPrefixes(){
      return Arrays.copyOf(paths, paths.length);
   }
   
   public boolean isLayoutPath(String resource) {
      for(String path : paths) {
         path = path.replace("\\", "/");
         
         if(!path.startsWith("/")) {
            path = "/" + path;
         }
         if(path.startsWith(resource)) {
            return true;
         }
      }
      return false;
   }
   
   public String getRealPath(File projectPath, String resource) { //  "/demo/mario/MarioGame.tern" -> "/demo/mario/src/mario/MarioGame.tern"
      if(resource != null) {
         File resourcePath = new File(projectPath, resource);
         
         if(!resourcePath.exists()) {
            for(String path : paths) {
               File file = new File(projectPath, path + "/" + resource);
               
               if(file.exists()) {
                  String relativePath = getRelativeFile(projectPath, file);
                  if(!relativePath.startsWith("/")) {
                     return "/" +relativePath;
                  }
                  return relativePath;
               }
            }
         }
      }
      return resource;
   }
   
   public String getDownloadPath(File projectPath, String resource) { // "/demo/mario/src/mario/MarioGame.tern" -> "/demo/mario/MarioGame.tern"
      if(resource != null) {
         if(!resource.startsWith("/")) {
            resource = "/" + resource;
         }
         for(String path : paths) {
            path = path.replace("\\", "/");
            
            if(!path.startsWith("/")) {
               path = "/" + path;
            }
            if(resource.startsWith(path)) {
               int length = path.length();
               return resource.substring(length);
            }
         }
      }
      return resource;
   }
   
   private String getRelativeFile(File root, File file) {
      return root.toURI().relativize(file.toURI()).getPath();
   }
}
