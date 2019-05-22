package org.ternlang.studio.core;

import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Produces;
import org.ternlang.core.Bug;

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
