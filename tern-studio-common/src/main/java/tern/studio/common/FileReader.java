package tern.studio.common;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FileReader {

   public static String readText(File file) throws Exception {
      byte[] binary = readBinary(file);
      return new String(binary, "UTF-8");
   }
   
   public static byte[] readBinary(File file) throws Exception {
      if(file.exists() && file.isFile()) {
         InputStream source = new FileInputStream(file);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[1024];
         int count = 0;
         
         try {
            while((count = source.read(data)) != -1) {
               buffer.write(data, 0, count);
            }
            return buffer.toByteArray();
         } finally {
            source.close();
         }
      }
      throw new IOException("Resource "  + file + " is not a file");   
   }
   
   public static Iterator<String> readLines(File file) throws Exception {
      if(file.exists() && file.isFile()) {
         return new FileLineIterator(file);
      }
      throw new IOException("Resource "  + file + " is not a file");    
   }
   
   private static class FileLineIterator implements Iterator<String>, Closeable {
      
      private final AtomicReference<String> reference;
      private final LineNumberReader iterator;
      private final InputStreamReader reader;
      private final AtomicBoolean active;
      private final InputStream source;
      private final File file;

      public FileLineIterator(File file) throws Exception {
         this.reference = new AtomicReference<String>();
         this.source = new FileInputStream(file);
         this.reader = new InputStreamReader(source, "UTF-8");
         this.iterator = new LineNumberReader(reader);
         this.active = new AtomicBoolean(true);
         this.file = file;
      }
      
      @Override
      public String next() {
         return reference.getAndSet(null);
      }

      @Override
      public boolean hasNext() {
         try {
            String line = reference.get();
            
            if(line == null) {
               if(active.get()) {
                  line = iterator.readLine();
                  
                  if(line == null) {
                     active.set(false);
                     reader.close();
                     return false;
                  }
                  reference.set(line);
               }
            }
            return line != null;
         } catch(Exception e) {
            try {
               active.set(false);
               reader.close();
            }catch(Exception close) {
            }
            throw new IllegalStateException("Error iterating over " +file);
         }
      }
      
      @Override
      public void close() {
         try {
            active.set(false);
            reader.close();
         }catch(Exception close) {
            throw new IllegalStateException("Error closing over " +file);
         }
      }
      
   }
}