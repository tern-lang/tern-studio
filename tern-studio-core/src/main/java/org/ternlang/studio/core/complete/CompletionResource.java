package org.ternlang.studio.core.complete;

import org.simpleframework.resource.annotation.Body;
import org.simpleframework.resource.annotation.Consumes;
import org.simpleframework.resource.annotation.POST;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.PathParam;
import org.simpleframework.resource.annotation.Produces;
import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.CompletionRequest;
import org.ternlang.studio.index.complete.CompletionResponse;

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