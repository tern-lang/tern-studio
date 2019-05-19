package org.ternlang.studio.build;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.service.annotation.GET;
import org.ternlang.service.annotation.Path;

import lombok.AllArgsConstructor;

@Path("/js")
@AllArgsConstructor
public class TypeScriptResource {
   
   private final TypeScriptService service;

   @GET
   @Path(".*.js")
   public void handle(Request request, Response response) {
      service.process(request, response);
   }

}