package tern.studio.service.project;

import static tern.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.common.resource.display.DisplayModelResolver;
import tern.studio.common.resource.template.TemplateEngine;
import tern.studio.common.resource.template.TemplateModel;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/")
public class ProjectSelectResource implements Resource {
   
   private static final String SELECT_RESOURCE = "select.vm";
   
   private final DisplayModelResolver resolver;
   private final TemplateEngine engine;
   private final Workspace workspace;
   
   public ProjectSelectResource(DisplayModelResolver resolver, Workspace workspace, TemplateEngine engine) {
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

      String text = engine.renderTemplate(model, SELECT_RESOURCE);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}