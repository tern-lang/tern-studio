package org.ternlang.studio.core.project;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.resource.annotation.GET;
import org.simpleframework.module.resource.annotation.Path;
import org.simpleframework.module.resource.template.TemplateEngine;
import org.simpleframework.module.resource.template.TemplateModel;
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
   @Path("$")
   @SneakyThrows
   public void select(Request request, Response response) {
      TemplateModel model = resolver.getModel();
      File root = workspace.getRoot();
      String name = root.getName();
      String version = VERSION.getValue();

      model.setAttribute("version", version);
      model.setAttribute("root", name);

      String text = engine.renderTemplate(model, SELECT_RESOURCE);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}