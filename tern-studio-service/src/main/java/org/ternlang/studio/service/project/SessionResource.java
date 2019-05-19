package org.ternlang.studio.service.project;

import java.io.PrintStream;

import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.ternlang.studio.resource.SessionConstants;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;

import lombok.SneakyThrows;

@Path("/session")
public class SessionResource {

   @GET
   @Path(".*")
   @SneakyThrows
   public void handle(Request request, Response response) {
      org.simpleframework.http.Path path = request.getPath();
      String target = request.getTarget();
      String[] segments = path.getSegments();
      PrintStream stream = response.getPrintStream();

      if(segments.length > 2) {
         String session = segments[1];
         String launch = target.replace("/session/" + session, "");
         String origin = request.getValue(Protocol.HOST);
         String address = String.format("http://%s%s", origin, launch);

         response.setStatus(Status.TEMPORARY_REDIRECT);
         response.setContentType("text/plain");
         response.setCookie(SessionConstants.SESSION_ID, session);
         response.setValue(Protocol.LOCATION, address);
         stream.println("Redirecting to " + address);
         response.close();
      } else {
         response.setStatus(Status.NOT_FOUND);
         stream.println("Could not share session for " + target);
         response.close();
      }
   }
}
