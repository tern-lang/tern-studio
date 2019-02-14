package org.ternlang.studio.service.agent.local;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;

import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachResponse;

// /debug/<project>/<host>/<port>/attach
@Path("/debug")
@AllArgsConstructor(onConstructor=@__({@Inject}))
public class LocalProcessResource {

   private final LocalProcessClient client;

   @GET
   @Path("/{project}/{host}/{port}/attach")
   @Produces("application/json")
   public Response attach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      AttachResponse response = client.attach(project, host, port);
      return Response.ok(response).build();
   }
   
   @GET
   @Path("/{project}/{host}/{port}/detach")
   @Produces("application/json")
   public Response detach(
         @PathParam("project") String project, 
         @PathParam("host") String host, 
         @PathParam("port") int port) throws Exception
   {
      DetachResponse response = client.detach(project, host, port);
      return Response.ok(response).build();
   }
}
