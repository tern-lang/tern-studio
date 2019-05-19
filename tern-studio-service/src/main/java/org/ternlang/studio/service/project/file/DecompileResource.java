package org.ternlang.studio.service.project.file;

import org.ternlang.studio.resource.action.annotation.CacheControl;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/resource")
@AllArgsConstructor
public class DecompileResource {

   private final DecompileService service;
   
   @GET
   @Produces("text/plain")
   @Path("/{project}/decompile/{jar}.jar/{module}/{name}.java")
   @CacheControl("no-cache, max-age=0, must-revalidate, proxy-revalidate")
   public byte[] decompile(
         @PathParam("project") String project, 
         @PathParam("jar") String jar,
         @PathParam("module") String module,
         @PathParam("name") String name)
   {
      FileResult result = service.decompile(project, 
            jar + ".jar", 
            module + "." + name);
      
      return result.getData();
   }
}
