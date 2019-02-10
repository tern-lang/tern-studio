package tern.studio.service.complete;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.index.complete.CompletionCompiler;
import tern.studio.index.complete.CompletionOutlineResponse;
import tern.studio.index.complete.CompletionRequest;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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