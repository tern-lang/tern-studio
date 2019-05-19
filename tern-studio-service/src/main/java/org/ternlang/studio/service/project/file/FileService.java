package org.ternlang.studio.service.project.file;

import org.ternlang.studio.project.FileCache;
import org.ternlang.studio.project.FileData;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.ContentTypeResolver;
import org.ternlang.studio.resource.action.annotation.Component;

import lombok.SneakyThrows;

@Component
public class FileService {
   
   private final ContentTypeResolver resolver;
   private final FileCache cache;

   public FileService(Workspace workspace, ContentTypeResolver resolver){
      this.cache = new FileCache(workspace);
      this.resolver = resolver;
   }

   @SneakyThrows
   public FileResult findFile(String project, String path) {
      FileData file = cache.getFile(project, path);
      String type = resolver.resolveType(path);

      try {
         byte[] resource = file.getByteArray();
         long lastModified = file.getLastModified();

         return new FileResult(type, resource, lastModified);
      }catch(Exception e) {
         byte[] resource = ("// No source found for " + path).getBytes();
         long time = System.currentTimeMillis();
         
         return new FileResult(type, resource, time);
      }
   }
}