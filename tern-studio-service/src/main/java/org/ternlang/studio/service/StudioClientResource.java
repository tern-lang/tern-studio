package org.ternlang.studio.service;

import org.ternlang.core.Bug;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/debugger")
@AllArgsConstructor
public class StudioClientResource {

   private final StudioClientLauncher launcher;

   @GET
   @Bug("Why does it need to be /debugger/ ??")
   @Path(".*")
   @Produces("text/plain")
   public boolean debug() throws Throwable {
      launcher.debug();
      return true;
   }
}
