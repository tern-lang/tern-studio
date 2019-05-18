package org.ternlang.studio.common.find.file;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter implements FilenameFilter {
   
   private final String[] extensions;
   
   public FileExtensionFilter(String[] extensions) {
      this.extensions = extensions;
   }

   @Override
   public boolean accept(File dir, String name) {
      String normal = name.toLowerCase();
      
      for(String extension : extensions) {
         if(normal.endsWith(extension)) {
            return true;
         }
      }
      return false;
   }

}