package tern.studio.service.complete;

import java.io.PrintStream;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
@ResourcePath("/type.*")
public class SearchTypeResource implements Resource {
   
   private final Workspace workspace;
   private final Gson gson;
   
   public SearchTypeResource(Workspace workspace) {
      this.gson = new GsonBuilder().setPrettyPrinting().create();
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String expression = SearchExpressionParser.parse(request);
      PrintStream out = response.getPrintStream();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = project.getClassLoader();
      thread.setContextClassLoader(classLoader);
      Map<String, SearchTypeResult> results = SearchTypeCollector.search(project, expression);
      String text = gson.toJson(results);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }
}
