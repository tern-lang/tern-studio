package org.ternlang.studio.common;

import java.io.File;
import java.io.FileFilter;

public class DirectoryWatcher {

   public static long lastModified(File path) {
      File[] fileList = path.listFiles(new FileFilter() {

         @Override
         public boolean accept(File file) {
            return file.isDirectory();
         }
      });
      long latestModification = path.lastModified();
      
      if(fileList != null) {
         for(File file : fileList) {
            long lastModified = latestModification;
            
            if(file.isDirectory()) {
               lastModified = lastModified(file);
            } else {
               lastModified = file.lastModified();
            }
            if(lastModified > latestModification) {
               latestModification = lastModified;
            }
         }
      }
      return latestModification;
   }
}
