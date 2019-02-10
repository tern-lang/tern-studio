package tern.studio.common.resource;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class FileResolver {

   private final FileManager manager;

   public FileResolver(FileManager manager) {
      this.manager = manager;
   }

   public Content resolveContent(String path) throws IOException {
      return manager.getContent(path);
   }
}