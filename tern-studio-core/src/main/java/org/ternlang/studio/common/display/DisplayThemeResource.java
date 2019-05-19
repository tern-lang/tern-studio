package org.ternlang.studio.common.display;

import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.Produces;

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