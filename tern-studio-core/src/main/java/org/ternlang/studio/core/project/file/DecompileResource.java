package org.ternlang.studio.core.project.file;

import org.ternlang.service.resource.annotation.CacheControl;
import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.service.resource.annotation.Produces;

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
      try {
         FileResult result = service.decompile(project, 
               jar + ".jar", 
               module + "." + name);
         
         return result.getData();
      } catch(Exception e) {
         return String.format("// could not decompile %s.%s", module, name).getBytes();
      }
   }
}
