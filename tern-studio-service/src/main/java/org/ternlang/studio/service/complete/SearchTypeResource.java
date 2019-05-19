package org.ternlang.studio.service.complete;

import java.util.Map;

import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;
import org.ternlang.studio.resource.action.annotation.QueryParam;

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
