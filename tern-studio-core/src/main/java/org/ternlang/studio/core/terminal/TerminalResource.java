package org.ternlang.studio.core.terminal;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.resource.annotation.GET;
import org.simpleframework.resource.annotation.Path;
import org.simpleframework.resource.template.TemplateEngine;
import org.simpleframework.resource.template.TemplateModel;
import org.ternlang.studio.common.SessionCookie;
import org.ternlang.studio.common.display.DisplayModelResolver;
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Path("/terminal")
@AllArgsConstructor
public class TerminalResource {
   
   private static final String TERMINAL_RESOURCE = "terminal.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final Workspace workspace;

   @GET
   @Path(".*")
   @SneakyThrows
   public void handle(Request request, Response response) {
      String session = SessionCookie.findOrCreate(request, response);
      TemplateModel model = resolver.getModel(session);
      File root = workspace.getRoot();
      String name = root.getName();
      String version = VERSION.getValue();

      model.setAttribute("version", version);
      model.setAttribute("root", name);

      String text = engine.renderTemplate(model, TERMINAL_RESOURCE);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}