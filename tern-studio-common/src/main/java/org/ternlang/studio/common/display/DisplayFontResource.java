package org.ternlang.studio.common.display;

import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Produces;

import lombok.AllArgsConstructor;

// /theme/<project>
@Path("/font")
@AllArgsConstructor
public class DisplayFontResource {

   private final DisplayFontService service;

   @GET
   @Path(".*")
   @Produces("text/css")
   public String style() throws Throwable {
      return service.style();
   }

}