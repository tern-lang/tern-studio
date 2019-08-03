package org.ternlang.studio.common.display;

import java.io.InputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.Content;
import org.simpleframework.resource.FileResolver;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;
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
   @Produces("text/css")
   public byte[] getStyleSheet(Request request, Response response) throws Exception {
      return match(request, response);  
   }
   
   @GET
   @Path("/js/.*.js")   
   @Produces("application/javascript")
   public byte[] getJavaScript(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/ttf/.*.ttf")   
   @Produces("font/ttf")
   public byte[] getFont(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/img/.*.(gif|png|jpg|ico)")  
   @Produces({"image/png", "image/jpeg", "image/gif", "image/x-icon"})
   public byte[] getImage(Request request, Response response) throws Exception {
      return match(request, response);
   }
   
   @GET
   @Path("/favicon.ico")   
   @Produces({"image/x-icon"})   
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