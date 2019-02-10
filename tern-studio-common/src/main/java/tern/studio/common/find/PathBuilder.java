package tern.studio.common.find;

import java.io.File;

public class PathBuilder {
   
   private final String root;
   
   public PathBuilder(String root) {
      this.root = root;
   }

   public String buildPath(File file) {
      try {
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(root, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         return resourcePath;
      }catch(Exception e) {
         throw new IllegalArgumentException("Could not build path from " + file, e);
      }
   }
}