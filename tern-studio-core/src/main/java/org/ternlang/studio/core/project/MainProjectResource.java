package org.ternlang.studio.core.project;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.annotation.Produces;
import org.simpleframework.resource.template.TemplateEngine;
import org.simpleframework.resource.template.TemplateModel;
import org.ternlang.studio.common.SessionCookie;
import org.ternlang.studio.common.display.DisplayModelResolver;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Path("/project")
@AllArgsConstructor
public class MainProjectResource  {
   
   private static final String PROJECT_RESOURCE = "project.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;

   @GET
   @Path("/[a-zA-Z].*")
   @SneakyThrows
   @Produces("text/html")
   public void handle(Request request, Response response) {
      String session = SessionCookie.findOrCreate(request, response);
      TemplateModel model = resolver.getModel(session);
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

      stream.print(text);
      stream.close();
   }
}