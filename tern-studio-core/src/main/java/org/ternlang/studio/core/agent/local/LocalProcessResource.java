package org.ternlang.studio.core.agent.local;

import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachResponse;

import lombok.AllArgsConstructor;

// /debug/<project>/<host>/<port>/attach
@Path("/debug")
@AllArgsConstructor
public class LocalProcessResource {

   private final LocalProcessClient client;

   @GET
   @Path("/{project}/{host}/{port}/attach")
   @Produces("application/json")
   public AttachResponse attach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      return client.attach(project, host, port);
   }
   
   @GET
   @Path("/{project}/{host}/{port}/detach")
   @Produces("application/json")
   public DetachResponse detach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      return client.detach(project, host, port);
   }
}
