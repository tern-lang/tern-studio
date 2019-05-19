package org.ternlang.studio.service.project.file;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.CacheControl;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

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
         
         response.setContentType(type);
         return result.getData();
      }
      return String.format("// could not find %s", path).getBytes();
   }
}