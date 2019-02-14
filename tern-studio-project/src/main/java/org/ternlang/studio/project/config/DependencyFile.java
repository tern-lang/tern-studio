package org.ternlang.studio.project.config;

import java.io.File;

public class DependencyFile {

   private final File file;
   private final String message;

   public DependencyFile(File file) {
      this(file, null);
   }

   public DependencyFile(File file, String message) {
      this.file = file;
      this.message = message;
   }
   
   public File getFile() {
      return file;
   }
   
   public String getMessage() {
      return message;
   }
   
   @Override
   public String toString() {
      return String.format("%s: %s", file, message);
   }
}
