package org.ternlang.studio.common.resource.template;

import java.util.List;

public class ChainTemplateEngine implements TemplateEngine {
   
   private final List<TemplateEngine> engines;
   
   public ChainTemplateEngine(List<TemplateEngine> engines) {
      this.engines = engines;
   }

   @Override
   public String renderTemplate(TemplateModel model, String template) throws Exception {
      for(TemplateEngine engine : engines) {
         if(engine.validTemplate(template)) {           
            return engine.renderTemplate(model, template);
         }
      }
      return null;
   }

   @Override
   public boolean validTemplate(String template) throws Exception {
      for(TemplateEngine engine : engines) {
         if(engine.validTemplate(template)) {           
            return true;
         }
      }
      return false;
   }

}