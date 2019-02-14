package org.ternlang.studio.build.console;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ConsoleReader {

   public static String read(InputStream stream) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      
      while(true) {
         int next = stream.read();
         
         if(next == -1) {
            int length = buffer.size();
            
            if(length == 0) {
               throw new EOFException("Console has been closed");
            }
            return buffer.toString("UTF-8");
         }
         buffer.write(next);
         
         if(next == '\n') {
            return buffer.toString("UTF-8");
         }
      }
   }
}