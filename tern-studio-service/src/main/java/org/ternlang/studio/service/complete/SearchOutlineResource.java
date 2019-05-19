package org.ternlang.studio.service.complete;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.CompletionOutlineResponse;
import org.ternlang.studio.index.complete.CompletionRequest;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.ternlang.studio.resource.action.annotation.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ResourcePath("/outline.*")
public class SearchOutlineResource implements Resource {
   
   private final Workspace workspace;
   private final Gson gson;
   
   public SearchOutlineResource(Workspace workspace) {
      this.gson = new GsonBuilder().setPrettyPrinting().create();
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
      CompletionCompiler compiler = new CompletionCompiler(project.getIndexDatabase());
      CompletionOutlineResponse result = compiler.completeOutline(context);
      String expression = result.getExpression();
      String details = result.getDetails();     
      String text = gson.toJson(result);
      
      response.setContentType("application/json");
      out.println(text);
      out.close();
      
      if(log.isDebugEnabled()) {
         log.debug("Expression: " + expression);
         log.debug("Tree: \n" + details);
      }
   }
}