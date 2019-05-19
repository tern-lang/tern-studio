package org.ternlang.studio.service.project.file;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.CacheControl;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/download")
@AllArgsConstructor
public class DownloadResource {
   
   private final DownloadService service;

   @GET
   @Produces("text/plain")
   @Path("/{project}/{path}")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] downloadFile(
         @PathParam("project") String project, 
         @PathParam("path") String path,
         Response response)
   {
      FileResult result = service.findFile(project, path);
      String type = result.getType();
      
      response.setContentType(type);
      return result.getData();
   }
}