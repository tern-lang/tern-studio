package org.ternlang.studio.core;

import org.ternlang.core.Bug;
import org.ternlang.service.annotation.GET;
import org.ternlang.service.annotation.Path;
import org.ternlang.service.annotation.Produces;

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
