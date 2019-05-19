package org.ternlang.studio.service;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.ternlang.studio.resource.action.annotation.Component;

import lombok.AllArgsConstructor;

@Component
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
