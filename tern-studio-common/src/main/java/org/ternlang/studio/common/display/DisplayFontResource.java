package org.ternlang.studio.common.display;

import org.ternlang.service.annotation.GET;
import org.ternlang.service.annotation.Path;
import org.ternlang.service.annotation.Produces;

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