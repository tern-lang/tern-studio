package org.ternlang.studio.core.project.file;

import org.simpleframework.http.Protocol;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.resource.annotation.CacheControl;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;

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
         long lastModified = result.getLastModified();
         
         response.setDate(Protocol.LAST_MODIFIED, lastModified);
         response.setContentType(type);
         return result.getData();
      }
      response.setStatus(Status.NOT_FOUND);
      return String.format("// could not find %s", path).getBytes();
   }
}