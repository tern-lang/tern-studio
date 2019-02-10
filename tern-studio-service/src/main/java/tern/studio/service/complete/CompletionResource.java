package tern.studio.service.complete;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.index.complete.CompletionCompiler;
import tern.studio.index.complete.CompletionRequest;
import tern.studio.index.complete.CompletionResponse;
import tern.studio.index.complete.FindConstructorsInScope;
import tern.studio.index.complete.FindForExpression;
import tern.studio.index.complete.FindInScopeMatching;
import tern.studio.index.complete.FindMethodReference;
import tern.studio.index.complete.FindPossibleImports;
import tern.studio.index.complete.FindTraitToImplement;
import tern.studio.index.complete.FindTypesToExtend;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

// /complete/<project>
@Slf4j
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
         log.info("Expression: " + expression);
         log.info("Tree: \n" + details);
      }
   }
}