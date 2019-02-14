package org.ternlang.studio.agent.runtime;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;
import org.ternlang.common.LazyBuilder;
import org.ternlang.common.LazyCache;
import org.ternlang.common.LeastRecentlyUsedCache;

public class RuntimeValueSource {

   private final LazyBuilder<String, RuntimeValue> builder;
   private final Cache<String, RuntimeValue> cache;
   private final Cache<String, String> values;

   public RuntimeValueSource(Class<? extends RuntimeValue>... types) {
      this.builder = new RuntimeAttributeBuilder(types);
      this.cache = new LazyCache<String, RuntimeValue>(builder);
      this.values = new LeastRecentlyUsedCache<String, String>(1000);
   }

   public String getAttribute(String name) {
      String value = values.fetch(name);

      if(value == null) {
         RuntimeValue attribute = cache.fetch(name);
         String result = attribute.getValue();
         values.cache(name, result);
         return result;
      }
      return value;
   }

   private static class RuntimeAttributeBuilder implements LazyBuilder<String, RuntimeValue> {

      private final List<Class<? extends RuntimeValue>> types;
      private final Cache<String, RuntimeValue> instances;
      private final AtomicBoolean loaded;

      public RuntimeAttributeBuilder(Class<? extends RuntimeValue>... types) {
         this.instances = new CopyOnWriteCache<String, RuntimeValue>();
         this.loaded = new AtomicBoolean();
         this.types = Arrays.asList(types);
      }

      @Override
      public RuntimeValue create(String key) {
         RuntimeValue value = instances.fetch(key);

         if (value == null) {
            return load(key);
         }
         return value;
      }

      private RuntimeValue load(String key) {
         try {
            if(!loaded.get()) {
               for(Class<? extends RuntimeValue> type : types) {
                  RuntimeValue value = load(type);
                  String name = value.getName();

                  if(name != null) {
                     instances.cache(name, value);
                  }
               }
               loaded.set(true);
            }
            return instances.fetch(key);
         } catch (Exception e) {
            e.printStackTrace();
         }
         return new EmptyValue(key);
      }

      private RuntimeValue load(Class<? extends RuntimeValue> type) {
         try {
            Constructor<? extends RuntimeValue> constructor = type.getDeclaredConstructor();

            if (!constructor.isAccessible()) {
               constructor.setAccessible(true);
            }
            return constructor.newInstance();
         } catch (Exception e) {
            e.printStackTrace();
         }
         return new EmptyValue();
      }
   }
}
