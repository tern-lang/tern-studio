package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.ternlang.studio.resource.action.annotation.Component;

public class ComponentMapper {
   
   private final Map<Class, Set<Class>> cache;
   
   public ComponentMapper() {
      this.cache = new ConcurrentHashMap<>();
   }
   
   public boolean accept(Class<?> type) {
      if(type != null) {
         return type.isAnnotationPresent(Component.class);
      }
      return false;
   }

   public Set<Class> expand(Object value) {
      if(value != null) {
         Class type = value.getClass();
         
         if(type != Object.class) {
            return expand(type);
         }
      }
      return Collections.emptySet();
   }
   
   public Set<Class> expand(Class type) {
      Set<Class> done = cache.get(type);
      
      if(done == null) {
         Set<Class> types = new HashSet<Class>();
         
         if(type != Object.class) {
            expand(type, types);
            cache.put(type, types);
         }
         return Collections.unmodifiableSet(types);
      }
      return Collections.unmodifiableSet(done);
   }
   
   public void expand(Class type, Set<Class> types) {
      if(types.add(type)) {
         Class[] interfaces = type.getInterfaces();
         Class base = type.getSuperclass();
      
         for(Class entry : interfaces)  {
            expand(entry, types);
         }
         if(base != Object.class && base != null) {
            expand(base, types);
         }
      }
   }
   
   public Predicate filter(String name) {
      return (object) -> {
         if(object != null) {
            Class<?> type = object.getClass();
            Component component = type.getAnnotation(Component.class);
            
            if(component != null) {
               return component.value().equals(name);
            }
         }
         return false;
      };
   }
}