package org.ternlang.studio.common.find.text;

import java.io.File;

public class TextFile {
   
   private final File file;
   private final String project;
   private final String path;
   
   public TextFile(File file, String project, String path) {
      this.project = project;
      this.file = file;
      this.path = path;
   }
   
   @Override
   public boolean equals(Object value) {
      if(value instanceof TextFile) {
         return equals((TextFile)value);
      }
      return false;
   }
   
   public boolean equals(TextFile value) {
      return value.file.equals(file);
   }
   
   @Override
   public int hashCode() {
      return file.hashCode();
   }

   public File getFile() {
      return file;
   }
   
   public String getProject(){
      return project;
   }

   public String getPath() {
      return path;
   }
   
   @Override
   public String toString(){
      return file.toString();
   }
}