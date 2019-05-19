package org.ternlang.studio.core.loader;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

public class ClassPathScanner {

   public static File findLocation(Class type) throws Exception {
      String file = extractName(type);
      URL location = type.getResource(file);

      if (location == null) {
         throw new IllegalStateException("Could not find '" + file + "'");
      }
      String resource = location.toString();

      if (resource.startsWith("file:")) {
         return convertFromFile(type, resource);
      }
      if (resource.startsWith("jar:file:")) {
         return convertFromJar(resource);
      }
      throw new IllegalStateException("Could not parse '" + resource + "'");
   }
   
   private static File convertFromFile(Class type, String resource) throws Exception {
      String directory = extractDirectory(type);
      URI location = URI.create(resource);
      String file = location.getRawSchemeSpecificPart();
      int index = file.indexOf(directory);
      
      if(index != -1) {
         file = file.substring(0, index);
      }
      return new File(file);
   }
   
   private static File convertFromJar(String resource) throws Exception {
      int index = resource.indexOf("!");

      if (index == -1) {
         throw new IllegalStateException("Could not determine source from '" + resource + "'");
      }
      String local = resource.substring(9, index);
      String decoded = URLDecoder.decode(local, "UTF-8");

      return new File(decoded);
   }
   
   private static String extractDirectory(Class type) throws Exception {
      String name = type.getName();
      int index = name.lastIndexOf('.');
     
      if(index != -1) {
         name = name.substring(0, index);
      }
      return name.replace('.', '/');
   }

   private static String extractName(Class type) throws Exception {
      String name = type.getName();
      int index = name.lastIndexOf('.');

      if (index != -1) {
         name = name.substring(index + 1);
      }
      return name + ".class";

   }
}