package org.ternlang.studio.service.complete;

import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.CompletionOutlineResponse;
import org.ternlang.studio.index.complete.CompletionRequest;
import org.ternlang.studio.resource.action.annotation.Body;
import org.ternlang.studio.resource.action.annotation.Consumes;
import org.ternlang.studio.resource.action.annotation.POST;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/outline")
@AllArgsConstructor
public class CompleteOutlineResource {
   
   private final CompletionService service;

   @POST
   @Path("/{project}")
   @Consumes("application/json")
   @Produces("application/json")
   public CompletionOutlineResponse complete(
         @PathParam("project") String name, 
         @Body CompletionRequest context) throws Throwable 
   {
      CompletionCompiler compiler = service.create(name);
      CompletionOutlineResponse result = compiler.completeOutline(context);
      String expression = result.getExpression();
      String details = result.getDetails();     
 
      if(log.isDebugEnabled()) {
         log.debug("Expression: " + expression);
         log.debug("Tree: \n" + details);
      }
      return result;
   }
}