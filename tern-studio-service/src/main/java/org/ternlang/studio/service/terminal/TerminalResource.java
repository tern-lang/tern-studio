package org.ternlang.studio.service.terminal;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.common.display.DisplayModelResolver;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.ternlang.studio.resource.template.TemplateEngine;
import org.ternlang.studio.resource.template.TemplateModel;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/terminal/.*")
public class TerminalResource implements Resource {
   
   private static final String TERMINAL_RESOURCE = "terminal.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final Workspace workspace;
   
   public TerminalResource(DisplayModelResolver resolver, Workspace workspace, TemplateEngine engine) {
      this.resolver = resolver;
      this.workspace = workspace;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      TemplateModel model = resolver.getModel();
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