package org.ternlang.studio.project;

import lombok.extern.slf4j.Slf4j;
import org.ternlang.studio.project.config.OperatingSystem;

import java.io.File;

@Slf4j
public class HomeDirectory {

   public static final String HOME_DIRECTORY = ".tern";

   public static File getInstallPath() {
      try {
         File installDir = OperatingSystem.resolveSystem().getInstallPath();

         if(!installDir.exists()) {
            installDir.mkdirs();
         }
         return installDir.getCanonicalFile();
      } catch(Exception e) {
         return getHomePath();
      }
   }

   public static File getInstallChildPath(String... path) {
      try {
         File result = getInstallPath();

         for(String segment : path) {
            result = new File(result, segment);
         }
         return result.getCanonicalFile();
      } catch(Exception e) {
         throw new IllegalStateException("Could not get directory", e);
      }
   }

   
   public static File getHomePath() {
      try {
         File installDir = OperatingSystem.resolveSystem().getHomePath();
         File homeDir = new File(installDir, HOME_DIRECTORY);
         
         if(!homeDir.exists()) {
            homeDir.mkdirs();
         }
         return homeDir.getCanonicalFile();
      } catch(Exception e) {
         throw new IllegalStateException("Could not get home directory", e);
      }
   }
   
   public static File getHomeChildPath(String... path) {
      try {
         File result = getHomePath();
         
         for(String segment : path) {
            result = new File(result, segment);
         }
         return result.getCanonicalFile();
      } catch(Exception e) {
         throw new IllegalStateException("Could not get directory", e);
      }
   }

}
