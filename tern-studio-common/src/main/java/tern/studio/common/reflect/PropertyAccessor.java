package tern.studio.common.reflect;

import java.lang.reflect.Method;

public class PropertyAccessor implements Accessor {

   private final Method method;

   public PropertyAccessor(String name, Class type) {
      this.method = getMethod(name, type);
   }

   @Override
   public Class getType() {
      return method.getReturnType();
   }

   @Override
   public <T> T getValue(Object source) {
      try {
         if(source != null) {
            return (T) method.invoke(source);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not acquire value", e);
      }
      return null;
   }

   protected static Method getMethod(String name, Class type) {
      Class base = type;
      
      while(type != null) {
         Method method = getMethod(name, type, Prefix.GET);

         if (method == null) {
            method = getMethod(name, type, Prefix.IS);
         }
         if (method != null) {
            return method;
         }
         type = type.getSuperclass();
      }
      throw new IllegalArgumentException("No property named '" + name + "' in " + base);
   }

   protected static Method getMethod(String name, Class type, Prefix prefix) {
      Method[] methods = type.getDeclaredMethods();
      String property = prefix.getProperty(name);
      Method match = null;

      for (Method method : methods) {
         Class[] parameterTypes = method.getParameterTypes();
         String methodName = method.getName();

         if (parameterTypes.length == 0) {
            if (methodName.equals(property)) {
               method.setAccessible(true);
               return method;
            }
         }
         if (parameterTypes.length == 1) {
            if(methodName.equals(property)) {
               method.setAccessible(true);               
               match = method;
            }
         }
      }
      return match;
   }

   private static enum Prefix {
      IS("is"), 
      GET("get");

      private final String prefix;

      private Prefix(String prefix) {
         this.prefix = prefix;
      }

      public String getProperty(String name) {
         char initial = name.charAt(0);
         char upperCase = Character.toUpperCase(initial);
         String end = name.substring(1);

         return String.format("%s%s%s", prefix, upperCase, end);
      }
   }
}