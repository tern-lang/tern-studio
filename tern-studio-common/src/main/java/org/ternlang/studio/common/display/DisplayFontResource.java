package org.ternlang.studio.common.display;

import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;

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