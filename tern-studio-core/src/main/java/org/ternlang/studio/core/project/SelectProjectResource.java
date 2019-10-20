package org.ternlang.studio.core.project;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
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
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Path("/")
@AllArgsConstructor
public class SelectProjectResource {
   
   private static final String SELECT_RESOURCE = "select.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final Workspace workspace;
   
   @GET
   @Path("($|project)")
   @SneakyThrows
   @Produces("text/html")
   public void select(Request request, Response response) {
      String session = SessionCookie.findOrCreate(request, response);
      TemplateModel model = resolver.getModel(session);
      File root = workspace.getRoot();
      String name = root.getName();
      String version = VERSION.getValue();

      model.setAttribute("version", version);
      model.setAttribute("root", name);

      String text = engine.renderTemplate(model, SELECT_RESOURCE);
      PrintStream stream = response.getPrintStream();

      stream.print(text);
      stream.close();
   }
}