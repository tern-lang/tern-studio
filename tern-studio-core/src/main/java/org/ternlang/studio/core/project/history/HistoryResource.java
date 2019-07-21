package org.ternlang.studio.core.project.history;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.annotation.Produces;

import lombok.AllArgsConstructor;

@Path("/history")
@AllArgsConstructor
public class HistoryResource {

   private final HistoryService service;
   
   @GET
   @Path("/.*")
   @Produces("application/json")
   public void handle(Request request, Response response) {
      service.handle(request, response);
   }
}