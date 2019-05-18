package org.ternlang.studio.resource.template;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ternlang.common.LeastRecentlyUsedMap;

public class PropertyTemplateFilter implements TemplateFilter {   

   private final Map<String, Variable> variables;
   private final Set<String> illegal;
   private final PropertyBinder binder;
   private final TemplateModel model;

   public PropertyTemplateFilter(TemplateModel model, PropertyBinder binder) {
      this.variables = new LeastRecentlyUsedMap<String, Variable>();
      this.illegal = new HashSet<String>();
      this.binder = binder;
      this.model = model;
   }

   @Override
   public Object process(String name) {  
      if(!illegal.contains(name)) {
         Variable variable = variables.get(name);
         
         if(variable == null) {
            variable = extract(name);
            variables.put(name, variable);
         }       
         try {
            return variable.getValue();
         } catch(Exception e) {
            illegal.add(name);
         }
      }
      return null;
   }   
   
   private Variable extract(String name) {
      Object value = model.getAttribute(name);
      
      if(value == null) {
         int index = name.indexOf('.');
         int length = name.length();
         
         if(index > 0) {
            String reference = name.substring(0, index);
            String property = name.substring(index + 1, length);
            Object source = model.getAttribute(reference);
            
            if(source != null) {
               return new PropertyVariable(source, name, property);
            }
         }
         return new NullVariable(name);
      }
      return new LocalVariable(value, name);
   }   

   public void clear() {
      variables.clear();
      illegal.clear();
   }  
   
   private abstract class Variable {       
    
      public boolean isNull() {
         return false;
      }

      public abstract String getName();
      public abstract Object getValue();   
   }
   
   private class NullVariable extends Variable {
      
      private String name;
      
      public NullVariable(String name) {
         this.name = name;
      }      
      
      @Override
      public boolean isNull() {
         return true;
      }      
      
      @Override
      public String getName() {
         return name;
      }
      
      @Override
      public Object getValue() {
         return null;
      }
   }
   
   private class LocalVariable extends Variable {
      
      private Object source;
      private String name;

      public LocalVariable(Object source, String name) {
         this.source = source;
         this.name = name;
      }
      
      @Override
      public String getName() {
         return name;
      }
      
      @Override
      public Object getValue() {
         return source;
      }
   }

   private class PropertyVariable extends Variable {
      
      private Object source;
      private String property;
      private String name;

      public PropertyVariable(Object source, String name, String property) {
         this.property = property;
         this.source = source;
         this.name = name;
      }
      
      @Override
      public String getName() {
         return name;
      }
      
      @Override
      public Object getValue() {
         return binder.getValue(property, source);
      }
   }
}