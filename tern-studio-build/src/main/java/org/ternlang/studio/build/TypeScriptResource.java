package org.ternlang.studio.build;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Intercept;
import org.simpleframework.module.resource.annotation.Path;

import lombok.AllArgsConstructor;

@Intercept("/")
@AllArgsConstructor
public class TypeScriptResource {
   
   private final TypeScriptService service;

   @GET
   @Path(".*.js")
   public byte[] handle(Request request, Response response) {
      return service.process(request, response);
   }

}