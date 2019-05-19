package org.ternlang.studio.resource.action.extract;

import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.build.DependencySystem;

public class DependencyListExtractor implements Extractor<List> {

   private final DependencySystem system;
   private final Class entry;

   public DependencyListExtractor(DependencySystem system, Class entry) {
      this.system = system;
      this.entry = entry;
   }

   @Override
   public List extract(Parameter parameter, Context context) {
      Class expect = parameter.getType();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();
      
      if(expect == List.class) {
         if(annotation != null) {
            return system.resolveAll(entry);
         }
         if(constructor) {
            return system.resolveAll(entry);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class expect = parameter.getType();
      Class element = parameter.getEntry();
      Inject annotation = parameter.getAnnotation(Inject.class);
      boolean constructor = parameter.isConstructor();

      if(annotation != null || constructor) {
         return expect == List.class && element == entry;
      }
      return false;
   }
}

