package org.ternlang.studio.core.complete;

import java.util.Map;

import org.ternlang.service.annotation.GET;
import org.ternlang.service.annotation.Path;
import org.ternlang.service.annotation.PathParam;
import org.ternlang.service.annotation.Produces;
import org.ternlang.service.annotation.QueryParam;

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
