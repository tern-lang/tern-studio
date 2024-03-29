package org.ternlang.studio.core.image.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.common.ClassPathReader;

@Component
public class ClassPathResourceLoader {
   
   private static final String DEFAULT_CLASS_EXTENSION = ".class";
   private static final String[] DEFAULT_RESOURCE_PREFIXES = {
      "org.ternlang.",
      "/grammar.txt",
      "/import.txt",
      "/instruction.txt"
   };
   
   private final Map<String, byte[]> cache;
   private final ClassPathParser parser;
   private final ClassPathFilter filter;
   
   public ClassPathResourceLoader() {
      this(DEFAULT_RESOURCE_PREFIXES, DEFAULT_CLASS_EXTENSION);
   }
   
   public ClassPathResourceLoader(String[] prefix, String extension) {
      this.cache = new ConcurrentHashMap<String, byte[]>();
      this.parser = new ClassPathParser(extension);
      this.filter = new ClassPathFilter(prefix);
   }

   public byte[] loadResource(String path) throws Exception {
      byte[] data = cache.get(path);
      
      if(data == null) {
         String type = parser.parse(path);
         
         if(filter.accept(type)) {
            data = ClassPathReader.findResourceAsArray(path);
            
            if(data != null) {
               cache.put(path, data);
            }
         }
      }
      return data;
   }
}