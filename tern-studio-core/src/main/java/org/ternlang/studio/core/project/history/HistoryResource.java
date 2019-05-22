package org.ternlang.studio.core.project.history;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;

import lombok.AllArgsConstructor;

@Path("/history.*")
@AllArgsConstructor
public class HistoryResource {

   private final HistoryService service;
   
   @GET
   public void handle(Request request, Response response) {
      service.handle(request, response);
   }
}