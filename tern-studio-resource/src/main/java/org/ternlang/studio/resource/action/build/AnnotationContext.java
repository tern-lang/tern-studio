package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class AnnotationContext {

   private Map<Class, Annotation> annotations;
   private String value;
   private boolean required;

   public AnnotationContext() {
      this.annotations = new HashMap<Class, Annotation>();
   }

   public Map<Class, Annotation> getAnnotations() {
      return annotations;
   }

   public void addAnnotation(Annotation annotation) {
      Class type = annotation.annotationType();
      annotations.put(type, annotation);
   }

   public String getDefault() {
      return value;
   }

   public void setDefault(String value) {
      this.value = value;
   }

   public boolean isRequired() {
      return required;
   }

   public void setRequired(boolean required) {
      this.required = required;
   }
}
