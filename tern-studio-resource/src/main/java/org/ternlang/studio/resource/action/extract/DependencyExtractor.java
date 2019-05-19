package org.ternlang.studio.resource.action.extract;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.build.DependencySystem;

public class DependencyExtractor implements Extractor<Object> {

   private final DependencySystem system;
   private final Class type;

   public DependencyExtractor(DependencySystem system, Class type) {
      this.system = system;
      this.type = type;
   }

   @Override
   public Object extract(Parameter parameter, Context context) {
      Class type = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();
      
      if(annotation != null) {
         String name = annotation.value();
         return extract(type, name);
      }
      if(constructor) {
         return extract(type, null);
      }
      return null;
   }
   
   private Object extract(Class type, String name) {
      try {
         if(name != null) {
            int length = name.length();
            
            if(length > 0) {
               return system.resolve(type, name);
            }
         }
         return system.resolve(type);
      } catch (Exception e) {
         return null;
      } 
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class expect = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();

      if(annotation != null || constructor) {
         return type == expect;
      }
      return false;
   }
}
