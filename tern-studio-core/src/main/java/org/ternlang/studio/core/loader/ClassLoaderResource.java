package org.ternlang.studio.core.loader;

import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.service.resource.annotation.Produces;

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