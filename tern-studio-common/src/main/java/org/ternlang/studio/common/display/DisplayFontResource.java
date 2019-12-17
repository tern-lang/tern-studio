package org.ternlang.studio.common.display;

import org.simpleframework.resource.annotation.CookieParam;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.HeaderParam;
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
   public String style(
         @CookieParam("SESSID") String session,
         @HeaderParam("User-Agent") String userAgent) throws Throwable 
   {
      String agent = String.valueOf(userAgent);
      boolean isWindows = agent.contains("Windows") || agent.contains("Win64");
      return service.style(session, isWindows);
   }

}