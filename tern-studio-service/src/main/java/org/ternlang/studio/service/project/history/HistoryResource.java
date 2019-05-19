package org.ternlang.studio.service.project.history;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;

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