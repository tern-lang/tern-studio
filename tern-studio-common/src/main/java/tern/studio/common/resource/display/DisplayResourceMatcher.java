package tern.studio.common.resource.display;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.FileDirectorySource;
import tern.studio.common.resource.Content;
import tern.studio.common.resource.FileResolver;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourceMatcher;
import org.springframework.stereotype.Component;

@Component
public class DisplayResourceMatcher implements ResourceMatcher {

   private final DisplayContentProcessor displayProcessor;
   private final FileResolver fileResolver;
   private final FileDirectorySource workspace;
   
   public DisplayResourceMatcher(DisplayContentProcessor displayProcessor, FileResolver fileResolver, FileDirectorySource workspace) {
      this.displayProcessor = displayProcessor;
      this.fileResolver = fileResolver;
      this.workspace = workspace;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      Content content = fileResolver.resolveContent(target);
      
      if(content != null) {
         InputStream stream = content.getInputStream();
         
         if(stream != null) {
            return new DisplayFileResource(displayProcessor, workspace);
         }
      }
      return null;
   }
}