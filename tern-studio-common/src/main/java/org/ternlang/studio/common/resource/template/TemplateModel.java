package org.ternlang.studio.common.resource.template;

import java.util.Collections;
import java.util.Map;

public class TemplateModel {
   
   private final Map<String, Object> attributes;
   
   public TemplateModel() {
      this(Collections.EMPTY_MAP);
   }
   
   public TemplateModel(Map<String, Object> attributes) {
      this.attributes = attributes;
   }
   
   public void setAttribute(String name, Object value) {
      attributes.put(name, value);
   }
   
   public Map<String, Object> getAttributes() {
      return attributes;
   }
   
   public Object getAttribute(String name) {
      return attributes.get(name);
   }
}