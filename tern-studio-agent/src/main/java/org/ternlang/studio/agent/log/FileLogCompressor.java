package org.ternlang.studio.agent.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

public class FileLogCompressor {
   
   private final FileLogRoller roller;
   private final long age;

   public FileLogCompressor(FileLogRoller roller) {
      this(roller, TimeUnit.DAYS.toMillis(2));
   }
   
   public FileLogCompressor(FileLogRoller roller, long age) {
      this.roller = roller;
      this.age = age;
   }
   
   public void compressFiles(File parent) {
      String[] fileNames = parent.list();
      
      for(String fileName : fileNames) {
         if(canCompress(parent, fileName)) {
            compressFile(parent, fileName);
         }
      }
   }
   
   private void compressFile(File parent, String fileName) {
      String compressFile = String.format("%s.gz", fileName);
      
      try {
         compressFile(parent, fileName, compressFile);
         deleteFile(parent, fileName);
      } catch(Exception e) {
         deleteFile(parent, compressFile);
      }
   }
   
   private void compressFile(File parent, String from, String to) throws IOException {
      File outputFile = new File(parent, to);
      File inputFile = new File(parent, from);
      FileInputStream input = new FileInputStream(inputFile);
      FileOutputStream output = new FileOutputStream(outputFile);
      GZIPOutputStream compressor = new GZIPOutputStream(output);
      
      copyFile(input, compressor); 
   }
   
   private void copyFile(InputStream from, OutputStream to) throws IOException {
      byte[] buffer = new byte[8192];
      int count = 0;
      
      while((count = from.read(buffer)) != -1) {
         to.write(buffer, 0, count);
      } 
      from.close();
      to.close();
   }   
   
   private void deleteFile(File parent, String fileName) {      
      File file = new File(parent, fileName);
      
      for(int i = 0; i < 10; i++) {
         if(!file.exists()) {
            break;
         }
         if(file.delete()) {
            break;
         }
      }
   }

   private boolean canCompress(File parent, String name) {
      File file = new File(parent, name);
      
      if(file.exists()) {
         long currentTime = System.currentTimeMillis();
         long ageThreshold = currentTime - age;
         long lastModified = file.lastModified();
         
         if(lastModified > ageThreshold) {
            return false;
         }
         if(name.endsWith(".gz")) {
            return false;
         }
         return roller.alreadyRolled(file);
      }
      return false;
   }
}