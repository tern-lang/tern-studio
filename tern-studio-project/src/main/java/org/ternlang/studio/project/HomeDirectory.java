package org.ternlang.studio.project;

import org.ternlang.studio.project.config.OperatingSystem;

import java.io.File;

public class HomeDirectory {

   public static final String HOME_DIRECTORY = ".tern";
   
   public static File getRootPath() {
      try {
         File installDir = OperatingSystem.resolveSystem().getInstallDirectory();
         File homeDir = new File(installDir, HOME_DIRECTORY);
         
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
