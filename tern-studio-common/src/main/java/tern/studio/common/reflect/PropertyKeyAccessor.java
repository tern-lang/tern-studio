package tern.studio.common.reflect;

import static tern.studio.common.reflect.PropertyAccessor.getMethod;

import java.lang.reflect.Method;
import java.util.Map;

public class PropertyKeyAccessor implements Accessor {
   
   private final Method method;
   private final Object key;
   private final Class entry;
   
   public PropertyKeyAccessor(String name, Class type, Class entry, Object key) {
      this.method = getMethod(name, type);
      this.entry = entry;
      this.key = key;
   }   

   @Override
   public Class getType() {
      return entry;
   }    

   @Override
   public <T> T getValue(Object source) {
      try {
         Object value = method.invoke(source);
         
         if(value != null) {
            return (T)((Map)value).get(key);
         }         
      } catch (Exception e) {
         throw new IllegalStateException("Could not acquire value for key '" + key + "'", e);
      }
      return null;
   }  
}