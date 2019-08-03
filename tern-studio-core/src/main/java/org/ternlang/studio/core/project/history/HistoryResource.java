package org.ternlang.studio.core.project.history;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;

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