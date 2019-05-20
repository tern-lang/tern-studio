package org.ternlang.studio.core.project.file;

import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.ternlang.service.resource.annotation.CacheControl;
import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.service.resource.annotation.Produces;

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
    
      if(result != null) {
         String type = result.getType();
         response.setContentType(type);
         return result.getData();
      }
      response.setStatus(Status.NOT_FOUND);
      return String.format("// could not find %s", path).getBytes();
   }
}