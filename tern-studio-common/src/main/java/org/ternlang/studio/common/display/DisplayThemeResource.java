package org.ternlang.studio.common.display;

import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Produces;

import lombok.AllArgsConstructor;

// /display/<project>
@Path("/display")
@AllArgsConstructor
public class DisplayThemeResource {
   
   private final DisplayThemeService service;
   
   @GET
   @Path("/{project}")
   @Produces("application/json")
   public DisplayDefinition theme() throws Throwable {
      return service.theme();
   }

}