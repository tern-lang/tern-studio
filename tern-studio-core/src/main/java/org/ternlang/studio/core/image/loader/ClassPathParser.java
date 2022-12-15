package org.ternlang.studio.core.image.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathParser {
   
   private final Map<String, String> cache;
   private final String extension;
   
   public ClassPathParser(String extension) {
      this.cache = new ConcurrentHashMap<String, String>();
      this.extension = extension;
   }

   public String parse(String path) {
      String type = cache.get(path);
      
      if(type == null) {
         int length = path.length();
         
         if(path.endsWith(extension)) {
            type = path.substring(0, length -6);
            type = type.replace('/', '.');
            
            if(type.startsWith(".")) {
               type = type.substring(1);
            }
            cache.put(path, type);
         } else {
            type = path;
            cache.put(path, type);
         }
            
      }
      return type;
   }
}