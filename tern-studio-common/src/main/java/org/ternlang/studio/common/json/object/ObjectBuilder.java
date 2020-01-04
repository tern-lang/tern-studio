package org.ternlang.studio.common.json.object;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

class ObjectBuilder {
   
   public ObjectBuilder() {
      super();
   }

   public Supplier<Object> create(Class<?> type, String name) { 
      try {
         Class<?> resolved = resolve(type);
         Constructor<?> factory = resolved.getDeclaredConstructor();

         factory.setAccessible(true);

         return new ObjectSupplier(factory, name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + name, e);
      }
   }
   
   private Class<?> resolve(Class<?> type) {
      if(Map.class.isAssignableFrom(type)) {
         if(type == Map.class) {
            return LinkedHashMap.class;
         }
         if(type == SortedMap.class) {
            return TreeMap.class;
         }
         if(type == ConcurrentMap.class) {
            return ConcurrentHashMap.class;
         }
         if(type == NavigableMap.class) {
            return TreeMap.class;
         }
      }
      return type;
   }

   private static class ObjectSupplier implements Supplier<Object> {

      private final Constructor factory;
      private final String name;

      public ObjectSupplier(Constructor factory, String name) {
         this.factory = factory;
         this.name = name;
      }

      @Override
      public Object get() {
         try {
            return factory.newInstance();
         } catch(Exception e) {
            throw new IllegalStateException("Could not create " + name, e);
         }
      }
   }
}
