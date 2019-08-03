package org.ternlang.studio.build;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Filter;
import org.simpleframework.resource.annotation.Path;

import lombok.AllArgsConstructor;

@Filter("/")
@AllArgsConstructor
public class TypeScriptResource {
   
   private final TypeScriptService service;

   @GET
   @Path(".*.js")
   public void handle(Request request, Response response) {
      service.process(request, response);
   }

}