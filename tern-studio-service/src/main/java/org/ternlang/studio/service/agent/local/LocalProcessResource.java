package org.ternlang.studio.service.agent.local;

import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachResponse;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

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
