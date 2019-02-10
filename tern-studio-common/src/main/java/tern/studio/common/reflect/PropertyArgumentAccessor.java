package tern.studio.common.reflect;

import static tern.studio.common.reflect.PropertyAccessor.getMethod;

import java.lang.reflect.Method;

public class PropertyArgumentAccessor implements Accessor {
   
   private final Object argument;
   private final Method method; 
   private final Class type;
   private final String name;
   
   public PropertyArgumentAccessor(String name, Class type, Object argument) {
      this.method = getMethod(name, type);
      this.argument = argument;
      this.type = type;
      this.name = name;
   }   

   @Override
   public Class getType() {
      return method.getReturnType();
   }    

   @Override
   public <T> T getValue(Object source) {
      try {
         if(source != null) {
            return (T)method.invoke(source, argument);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not acquire value for '" + name + "'", e);         
      }
      return null;
   }
}