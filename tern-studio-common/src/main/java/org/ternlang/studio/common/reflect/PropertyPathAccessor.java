package org.ternlang.studio.common.reflect;

import static org.ternlang.studio.common.reflect.PropertyAccessor.getMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyPathAccessor implements Accessor {
   
   private final Accessor[] accessors;
   
   public PropertyPathAccessor(String path, Class type) throws Exception  {
      this.accessors = getPath(path, type);
   }
   
   @Override
   public Class getType() {
      return accessors[accessors.length - 1].getClass();
   }

   @Override
   public <T> T getValue(Object source) {
      Object value = source;
      
      for(int i = 0; i < accessors.length; i++) {
         value = accessors[i].getValue(value);
         
         if(value == null) {
            return null;
         }
      }
      return (T)value;
   }
   
   private  Accessor[] getPath(String path, Class type) throws Exception {     
      String[] parts = path.split("\\.");
      Class current = type;
      
      if(parts.length > 0) {
         Accessor[] accessors = new Accessor[parts.length];
         
         for(int i = 0; i < parts.length; i++) {
            Accessor accessor = getAccessor(parts[i], current);
            
            current = accessor.getType();  
            accessors[i] = accessor;       
         }      
         return accessors;
      }
      return new Accessor[0];
   }
   
   private Accessor getAccessor(String expression, Class type) throws Exception {
      Pattern pattern = Pattern.compile("([a-zA-Z]+[a-zA-Z0-9_]+)\\[(\\w+)\\]");     
      Matcher matcher = pattern.matcher(expression);            
            
      if(type == Object.class) {
         return getLazyAccessor(expression, type);
      }
      if(matcher.matches()) {
         String name = matcher.group(1);
         String argument = matcher.group(2);

         return getIndirectAccessor(name, argument, type);
      }
      return getDirectAccessor(expression, type);
   }
   
   private Accessor getLazyAccessor(String expression, Class type) throws Exception {
      if(type == Object.class) {
         return new PropertyLazyAccessor(expression, type);
      }
      throw new IllegalStateException("Lazy access for '" + expression + "' to " + type + " not allowed");
   }
   
   private Accessor getDirectAccessor(String name, Class type) throws Exception  {
      Method method = getMethod(name, type);      
      Class[] parameters = method.getParameterTypes();
      
      if(parameters.length == 0) {
         return new PropertyAccessor(name, type);
      }
      throw new IllegalStateException("Access to '" + name + "' on " + type + " requires arguments");
   }   
   
   private Accessor getIndirectAccessor(String name, String argument, Class type) throws Exception {
      Method method = getMethod(name, type);
      Class result = method.getReturnType();
      Class[] parameters = method.getParameterTypes();
      
      if(result.isArray()) {
         Class entry = result.getComponentType();
         Integer index = Integer.parseInt(argument);
         
         return new PropertyIndexAccessor(name, type, entry, index);
      }
      if(List.class.isAssignableFrom(result)) {
         Class entry = Introspector.getReturnDependent(method);
         Integer index = Integer.parseInt(argument);
         
         return new PropertyIndexAccessor(name, type, entry, index); 
      }
      if(Map.class.isAssignableFrom(result)) {
         Class[] dependents = Introspector.getReturnDependents(method);
         Object key = StringConverter.convert(dependents[0], argument);
         Class entry = dependents[1];
         
         return new PropertyKeyAccessor(name, type, entry, key);
      }
      if(parameters.length == 1) {
         Class parameter = parameters[0];
         Object value = StringConverter.convert(parameter, argument);                 
         
         return new PropertyArgumentAccessor(name, type, value);
      }
      throw new RuntimeException("No know accessor for " + name + "[" + argument + "] on " + type);
   }
}