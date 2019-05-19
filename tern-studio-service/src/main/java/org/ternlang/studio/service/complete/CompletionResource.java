package org.ternlang.studio.service.complete;

import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.CompletionRequest;
import org.ternlang.studio.index.complete.CompletionResponse;
import org.ternlang.studio.resource.action.annotation.Body;
import org.ternlang.studio.resource.action.annotation.Consumes;
import org.ternlang.studio.resource.action.annotation.POST;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.PathParam;
import org.ternlang.studio.resource.action.annotation.Produces;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// /complete/<project>
@Slf4j
@Path("/complete")
@AllArgsConstructor
public class CompletionResource {

   private final CompletionService service;

   @POST
   @Path("/{project}")
   @Consumes("application/json")
   @Produces("application/json")
   public CompletionResponse complete(
         @PathParam("project") String name, 
         @Body CompletionRequest context) throws Throwable 
   {
      long start = System.currentTimeMillis();
      CompletionCompiler compiler = service.create(name);
      CompletionResponse result = compiler.completeExpression(context);
      String expression = result.getExpression();
      String details = result.getDetails();     
      
      if(log.isInfoEnabled()) {
         long finish = System.currentTimeMillis();

         log.info("Expression: " + expression);
         log.info("Duration: " + (finish - start) + "ms");
         log.info("Tree: \n" + details);
      }
      return result;
   }
}