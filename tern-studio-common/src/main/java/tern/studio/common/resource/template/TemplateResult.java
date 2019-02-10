package tern.studio.common.resource.template;

import java.util.Collections;
import java.util.Map;

public class TemplateResult {
   
   private final TemplateModel model;
   private final String template;
   
   public TemplateResult(String template) {
      this(template, Collections.EMPTY_MAP);
   }
   
   public TemplateResult(String template, Map<String, Object> values) {
      this.model = new TemplateModel(values);
      this.template = template;
   }
   
   public String getTemplate() {
      return template;
   }
   
   public TemplateModel getModel() {
      return model;
   }
}