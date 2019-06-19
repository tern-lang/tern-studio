package org.ternlang.studio.common.display;

import java.io.InputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.Content;
import org.simpleframework.module.resource.FileResolver;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.ternlang.studio.common.FileDirectorySource;

import lombok.AllArgsConstructor;

@Path("/")
@AllArgsConstructor
public class DisplayResource {

   private final DisplayContentProcessor displayProcessor;
   private final FileResolver fileResolver;
   private final FileDirectorySource workspace;

   @GET
   @Path("/css/.*.css")   
   public byte[] getStyleSheet(Request request, Response response) throws Exception {
      return match(request, response);  
   }
   
   @GET
   @Path("/js/.*.js")   
   public byte[] getJavaScript(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/ttf/.*.ttf")   
   public byte[] getFont(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/img/.*.(gif|png|jpg|ico)")   
   public byte[] getImage(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/favicon.ico")   
   public byte[] getIcon(Request request, Response response) throws Exception {
      return match(request, response);
   }
    
   private byte[] match(Request request, Response response) throws Exception {
      org.simpleframework.http.Path path = request.getPath();
      String target = path.getPath();
      Content content = fileResolver.resolveContent(target);
      
      if(content != null) {
         InputStream stream = content.getInputStream();
         
         if(stream != null) {
            return new DisplayFileResource(displayProcessor, workspace).handle(request, response);
         }
      }
      return null;
   }
}