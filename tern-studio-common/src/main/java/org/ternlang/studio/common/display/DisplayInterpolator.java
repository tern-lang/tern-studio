package org.ternlang.studio.common.display;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.resource.template.TemplateEngine;
import org.simpleframework.resource.template.TemplateModel;

@Component
public class DisplayInterpolator {
   
   private final DisplayModelResolver modelResolver;
   private final TemplateEngine engine;
   
   public DisplayInterpolator(DisplayModelResolver modelResolver, TemplateEngine engine) {
      this.modelResolver = modelResolver;
      this.engine = engine;
   }

   public InputStream interpolate(String session, String template) throws Exception {
      TemplateModel model = modelResolver.getModel(session);
      String result = engine.renderTemplate(model, template);
      
      if(result == null) {
         throw new IllegalStateException("Could not render template " + template);
      }
      byte[] data = result.getBytes("UTF-8");
      
      return new ByteArrayInputStream(data);
   }
}