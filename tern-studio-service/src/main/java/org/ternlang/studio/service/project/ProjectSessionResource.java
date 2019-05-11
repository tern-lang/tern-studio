package org.ternlang.studio.service.project;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.resource.Resource;
import org.ternlang.studio.common.resource.ResourcePath;
import org.ternlang.studio.common.resource.SessionConstants;
import org.ternlang.studio.service.StudioClientLauncher;

@Slf4j
@Component
@ResourcePath("/session/.*")
public class ProjectSessionResource implements Resource {

   private final StudioClientLauncher launcher;

   public ProjectSessionResource(StudioClientLauncher launcher) {
      this.launcher = launcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
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
