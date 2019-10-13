package org.ternlang.studio.common.display;

import org.simpleframework.resource.annotation.CookieParam;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;

import lombok.AllArgsConstructor;

// /display/<project>
@Path("/display")
@AllArgsConstructor
public class DisplayThemeResource {
   
   private final DisplayThemeService service;
   
   @GET
   @Path("/{project}")
   @Produces("application/json")
   public DisplayDefinition theme(@CookieParam("SESSID") String session) throws Throwable {
      return service.theme(session);
   }

}