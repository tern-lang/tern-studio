package org.ternlang.studio.service.complete;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.CompletionRequest;
import org.ternlang.studio.index.complete.CompletionResponse;
import org.ternlang.studio.index.complete.FindConstructorsInScope;
import org.ternlang.studio.index.complete.FindForExpression;
import org.ternlang.studio.index.complete.FindInScopeMatching;
import org.ternlang.studio.index.complete.FindMethodReference;
import org.ternlang.studio.index.complete.FindPossibleImports;
import org.ternlang.studio.index.complete.FindTraitToImplement;
import org.ternlang.studio.index.complete.FindTypesToExtend;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

// /complete/<project>
@Slf4j
@org.ternlang.studio.resource.action.annotation.Component
@Component
@ResourcePath("/complete.*")
public class CompletionResource implements Resource {

   private final Workspace workspace;
   private final Gson gson;
   
   public CompletionResource(Workspace workspace) {
      this.gson = new Gson();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      long start = System.currentTimeMillis();
      PrintStream out = response.getPrintStream();
      String content = request.getContent();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      ClassLoader classLoader = project.getClassLoader();
      Thread thread = Thread.currentThread();
      thread.setContextClassLoader(classLoader);
      CompletionRequest context = gson.fromJson(content, CompletionRequest.class);
      CompletionCompiler compiler = new CompletionCompiler(project.getIndexDatabase(),
            FindConstructorsInScope.class,
            FindPossibleImports.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class,
            FindForExpression.class,
            FindInScopeMatching.class,
            FindMethodReference.class);
      
      CompletionResponse result = compiler.completeExpression(context);
      String expression = result.getExpression();
      String details = result.getDetails();     
      String text = gson.toJson(result);
      
      response.setContentType("application/json");
      out.println(text);
      out.close();
      
      if(log.isInfoEnabled()) {
         long finish = System.currentTimeMillis();

         log.info("Expression: " + expression);
         log.info("Duration: " + (finish - start) + "ms");
         log.info("Tree: \n" + details);
      }
   }
}