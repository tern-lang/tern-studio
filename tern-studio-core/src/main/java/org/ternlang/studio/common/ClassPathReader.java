package org.ternlang.studio.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.core.io.ClassPathResource;

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
      ClassLoader loader = ClassPathResource.class.getClassLoader();
      String location = path;

      if (location.startsWith("/")) {
         location = path.substring(1);
      }
      return new ClassPathResource(location, loader).getInputStream();
   }

   public static Reader findResourceAsReader(String path) throws Exception {
      InputStream stream = findResourceAsStream(path);

      if (stream != null) {
         return new InputStreamReader(stream, "UTF-8");
      }
      return null;
   }
}
