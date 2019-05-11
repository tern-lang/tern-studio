package org.ternlang.studio.service.project;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.resource.Resource;
import org.ternlang.studio.common.resource.ResourcePath;
import org.ternlang.studio.service.StudioClientLauncher;

@Slf4j
@Component
@ResourcePath("/launch/.*")
public class ProjectWindowResource implements Resource {

   private final StudioClientLauncher launcher;

   public ProjectWindowResource(StudioClientLauncher launcher) {
      this.launcher = launcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      String[] segments = path.getSegments();

      if(segments.length > 3) {
         String launchPath = path.getPath(3);
         launcher.launch("http://" + segments[1] + ":" + segments[2] + launchPath);
      }
      response.setStatus(Status.CREATED);
      response.close();
   }
}
