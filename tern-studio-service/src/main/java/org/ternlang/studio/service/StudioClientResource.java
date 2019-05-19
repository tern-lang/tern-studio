package org.ternlang.studio.service;

import lombok.AllArgsConstructor;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.springframework.stereotype.Component;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;

@Component
@org.ternlang.studio.resource.action.annotation.Component
@AllArgsConstructor
@ResourcePath("/debugger")
public class StudioClientResource implements Resource {

   private final StudioClientLauncher launcher;

   @Override
   public void handle(Request request, Response response) throws Throwable {
      response.setStatus(Status.OK);
      launcher.debug();
   }
}
