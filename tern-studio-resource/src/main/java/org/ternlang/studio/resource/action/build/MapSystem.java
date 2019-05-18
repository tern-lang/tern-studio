package org.ternlang.studio.resource.action.build;

import java.util.Map;
import java.util.Objects;

public class MapSystem implements DependencySystem {
   
   private final Map<String, Object> components;
   
   public MapSystem(Map<String, Object> components) {
      this.components = components;
   }

   @Override
   public Object getDependency(Class type) {
      return components.entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .filter(Objects::nonNull)
            .filter(type::isInstance)
            .findFirst()
            .orElse(null);
   }

   @Override
   public Object getDependency(Class type, String name) {
      Object component = components.get(name);
      
      if(type.isInstance(component)) {
         return component;
      }
      return null;
   }
}
