package tern.studio.common.resource.template;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import tern.studio.common.resource.Content;
import tern.studio.common.resource.FileResolver;

public class TemplateFinder {

   private final FileResolver resolver;
   private final String prefix;

   public TemplateFinder(FileResolver resolver, String prefix) {
      this.resolver = resolver;
      this.prefix = prefix;
   }
   
   public Content findContent(String path) throws IOException {
      String realPath = findPath(path);

      if (realPath != null) {
         return resolver.resolveContent(realPath);
      }
      return null;
   }  

   public String findPath(String path) throws IOException {
      List<String> searchPath = searchPath(path);

      for(String realPath : searchPath) {
         Content content = resolver.resolveContent(realPath);
        
         if(content != null) {
            return realPath;
         }
      }
      return null;
   }

   private List<String> searchPath(String path) throws IOException {
      if (prefix != null) {
         String original = path;
         
         if (path.startsWith("/")) {
            path = path.substring(1);
         }         
         if (!path.startsWith(prefix)) {
            path = prefix + path;
         }
         return Arrays.asList(path, original);
      }
      return Arrays.asList(path);
   }
}