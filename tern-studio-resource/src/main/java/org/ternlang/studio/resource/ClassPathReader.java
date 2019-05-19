package org.ternlang.studio.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ClassPathReader {

   public static byte[] findResource(String path) throws Exception {
      InputStream input = findResourceAsStream(path);

      if (input != null) {
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int count = 0;

         try {
            while ((count = input.read(buffer)) != -1) {
               output.write(buffer, 0, count);
            }
            output.close();
         } finally {
            input.close();
         }
         return output.toByteArray();
      }
      return null;
   }

   public static InputStream findResourceAsStream(String path) throws Exception {
      ClassLoader loader = ClassPathReader.class.getClassLoader();
      InputStream stream = loader.getResourceAsStream(path);
      
      if(stream == null) {
         if (path.startsWith("/")) {
            path = path.substring(1);
         } else {
            path = "/" + path;
         }
      }
      return loader.getResourceAsStream(path);
   }

   public static Reader findResourceAsReader(String path) throws Exception {
      InputStream stream = findResourceAsStream(path);

      if (stream != null) {
         return new InputStreamReader(stream, "UTF-8");
      }
      return null;
   }
}
