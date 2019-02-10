package tern.studio.common.resource.display;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import tern.studio.common.resource.template.TemplateEngine;
import tern.studio.common.resource.template.TemplateModel;
import org.springframework.stereotype.Component;

@Component
public class DisplayInterpolator {
   
   private final DisplayModelResolver modelResolver;
   private final TemplateEngine engine;
   
   public DisplayInterpolator(DisplayModelResolver modelResolver, TemplateEngine engine) {
      this.modelResolver = modelResolver;
      this.engine = engine;
   }

   public InputStream interpolate(String template) throws Exception {
      TemplateModel model = modelResolver.getModel();
      String result = engine.renderTemplate(model, template);
      
      if(result == null) {
         throw new IllegalStateException("Could not render template " + template);
      }
      byte[] data = result.getBytes("UTF-8");
      
      return new ByteArrayInputStream(data);
   }
}