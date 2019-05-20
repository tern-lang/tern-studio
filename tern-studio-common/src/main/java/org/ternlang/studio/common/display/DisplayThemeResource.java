package org.ternlang.studio.common.display;

import org.ternlang.service.resource.annotation.GET;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.Produces;

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