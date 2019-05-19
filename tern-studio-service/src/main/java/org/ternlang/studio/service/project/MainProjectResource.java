package org.ternlang.studio.service.project;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.common.display.DisplayModelResolver;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.template.TemplateEngine;
import org.ternlang.studio.resource.template.TemplateModel;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Path("/project")
@AllArgsConstructor
public class MainProjectResource  {
   
   private static final String PROJECT_RESOURCE = "project.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;

   @GET
   @Path(".*")
   @SneakyThrows
   public void handle(Request request, Response response) {
      TemplateModel model = resolver.getModel();
      org.simpleframework.http.Path path = request.getPath(); // /project/<project-name>/<project-path>
      String projectPrefix = path.getPath(1, 2); // /<project-name>
      String projectDirectory = path.getPath(1); // /<project-name>
      String projectName = projectPrefix.substring(1); // <project-name>
      String version = VERSION.getValue();

      model.setAttribute("version", version);
      model.setAttribute("project", projectName);
      model.setAttribute("projectDirectory", projectDirectory);
      String text = engine.renderTemplate(model, PROJECT_RESOURCE);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}