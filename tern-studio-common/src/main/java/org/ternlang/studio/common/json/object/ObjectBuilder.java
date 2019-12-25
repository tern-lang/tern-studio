package org.ternlang.studio.common.json.object;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

class ObjectBuilder {
   
   public ObjectBuilder() {
      super();
   }

   public Supplier<Object> create(Class<?> type, String name) {
      try {
         Constructor<?> factory = type.getDeclaredConstructor();

         factory.setAccessible(true);

         return new ObjectSupplier(factory, name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create " + name, e);
      }
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
