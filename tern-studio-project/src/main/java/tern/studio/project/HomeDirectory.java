package tern.studio.project;

import java.io.File;

public class HomeDirectory {

   public static final String HOME_DIRECTORY = ".tern";
   
   public static File getRootPath() {
      try {
         String userDir = System.getProperty("user.home");
         File homeDir = new File(userDir, HOME_DIRECTORY);
         
         if(!homeDir.exists()) {
            homeDir.mkdirs();
         }
         return homeDir.getCanonicalFile();
      } catch(Exception e) {
         throw new IllegalStateException("Could not get home directory", e);
      }
   }
   
   public static File getPath(String... path) {
      try {
         File result = getRootPath();
         
         for(String segment : path) {
            result = new File(result, segment);
         }
         return result.getCanonicalFile();
      } catch(Exception e) {
         throw new IllegalStateException("Could not get directory", e);
      }
   }

}
