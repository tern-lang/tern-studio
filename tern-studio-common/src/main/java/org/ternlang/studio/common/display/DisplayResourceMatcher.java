package org.ternlang.studio.common.display;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.service.annotation.Component;
import org.ternlang.service.resource.Content;
import org.ternlang.service.resource.FileResolver;
import org.ternlang.service.resource.Resource;
import org.ternlang.service.resource.ResourceMatcher;
import org.ternlang.studio.common.FileDirectorySource;

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