package org.ternlang.studio.core.complete;

import java.util.Map;

import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.simpleframework.resource.annotation.QueryParam;

import lombok.AllArgsConstructor;

@Path("/type")
@AllArgsConstructor
public class SearchTypeResource {
   
   private final SearchTypeService service;

   @GET
   @Path("/{project}")
   @Produces("application/json")
   public Map<String, SearchTypeResult> search(
         @PathParam("project") String name,
         @QueryParam("expression") String expression)
   {
      return service.search(name, expression);
   }
}
