package org.ternlang.studio.core.project.file;

import org.simpleframework.http.Protocol;
import org.simpleframework.http.Response;
import org.simpleframework.resource.annotation.CacheControl;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/resource")
@AllArgsConstructor
public class FileResource {
   
   private final FileService service;

   @GET
   @Produces("text/plain")
   @Path("/{project}/{path}")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] findFile(
         @PathParam("project") String project, 
         @PathParam("path") String path,
         Response response)
   {
      FileResult result = service.findFile(project, path);
      
      if(result != null) {
         String type = result.getType();
         long lastModified = result.getLastModified();
         
         response.setDate(Protocol.LAST_MODIFIED, lastModified);
         response.setContentType(type);
         return result.getData();
      }
      return String.format("// could not find %s", path).getBytes();
   }
}