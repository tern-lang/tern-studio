package org.ternlang.studio.service.loader;

import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Path("/class")
@AllArgsConstructor
public class ClassLoaderResource {
   
   private final ClassPathResourceLoader loader;

   @GET
   @Path("/{name}")
   @SneakyThrows
   @Produces("application/octet-stream")
   public byte[] handle(@PathParam("name") String name) {
      return loader.loadResource(name); 
   }
}