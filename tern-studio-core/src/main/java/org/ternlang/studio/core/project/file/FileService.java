package org.ternlang.studio.core.project.file;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.resource.MediaTypeResolver;
import org.ternlang.studio.project.FileCache;
import org.ternlang.studio.project.FileData;
import org.ternlang.studio.project.Workspace;

import lombok.SneakyThrows;

@Component
public class FileService {
   
   private final MediaTypeResolver resolver;
   private final FileCache cache;

   public FileService(Workspace workspace, MediaTypeResolver resolver){
      this.cache = new FileCache(workspace);
      this.resolver = resolver;
   }

   @SneakyThrows
   public FileResult findFile(String project, String path) {
      FileData file = cache.getFile(project, path);

      if(file != null) {
         try {
            String type = resolver.resolveType(path);
            byte[] resource = file.getByteArray();
            long lastModified = file.getLastModified();
   
            return new FileResult(type, resource, lastModified);
         }catch(Exception e) {
            return null;
         }
      }
      return null;
   }
}