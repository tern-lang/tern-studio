package org.ternlang.studio.agent.log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLogAppender {

   private FileWriter appender;
   private PrintWriter writer;
   private File file;
   private boolean append;
   
   public FileLogAppender(File file) {
      this(file, true);
   }
   
   public FileLogAppender(File file, boolean append) {
      this.append = append;
      this.file = file;
   }
   
   public void append(Object text) {
      append(text, null);
   }
   
   public void append(Object text, Throwable cause) {
      try {
         if(!file.exists() || writer == null || writer.checkError()) {
            File parent = file.getParentFile();
            
            if(!parent.exists()) {
               parent.mkdirs();
            }
            appender = new FileWriter(file, append);
            writer = new PrintWriter(appender);
         }
         writer.print(text);
        
         if(cause != null) {
            writer.print(": ");
            cause.printStackTrace(writer);
         } else {
            writer.println();
         }
         writer.flush();
      }catch(Exception e) {
         throw new IllegalStateException("Could not write to file '" + file + "'", e);
      }
   }
   
   public void close() {
      try {
         if(writer != null) {
            writer.flush();
            writer.close();
            writer = null;
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not close file '" + file + "'", e);
      }
   }
}