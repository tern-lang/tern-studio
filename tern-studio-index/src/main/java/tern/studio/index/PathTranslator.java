package tern.studio.index;

import java.io.File;

public class PathTranslator {
   
   private final String[] prefixes;
   
   public PathTranslator(String... paths) {
      this.prefixes = new String[paths.length];
      
      for(int i = 0; i < paths.length; i++) {
         prefixes[i] = paths[i].replace("\\", "/");
         
         if(!prefixes[i].startsWith("/")) {
            prefixes[i] = "/" + prefixes[i];
         }
      }
   }
   
   public String getRealPath(File root, String resource) { //  "/demo/mario/MarioGame.snap" -> "/demo/mario/src/mario/MarioGame.snap"
      if(resource != null) {
         File file = new File(root, resource);
         
         if(!file.exists()) {
            for(String prefix : prefixes) {
               File possible = new File(root, prefix + "/" + resource);
               
               if(possible.exists()) {
                  String relative = getRelativeFile(root, possible);
                  
                  if(!relative.startsWith("/")) {
                     return "/" + relative;
                  }
                  return relative;
               }
            }
         }
      }
      return resource;
   }
   
   public String getScriptPath(File root, String resource) {
      if(resource != null) {
         for(String prefix : prefixes) {
            if(resource.startsWith(prefix)) {
               int length = prefix.length();
               
               if(length > 0) {
                  return resource.substring(length);
               }
               return resource;
            }
         }
      }
      return resource;
   }
   
   private String getRelativeFile(File root, File file) {
      return root.toURI().relativize(file.toURI()).getPath();
   }
}
