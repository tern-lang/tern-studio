package tern.studio.agent;

import java.util.HashMap;
import java.util.Map;

import tern.core.scope.Model;

public class ProcessModel implements Model {
   
   public static final String SHORT_ARGUMENTS = "args";
   public static final String LONG_ARGUMENTS = "arguments";
   
   private final Map<String, Object> values;
   private final Model model;
   
   public ProcessModel(Model model) {
      this.values = new HashMap<String, Object>();
      this.model = model;
   }
   
   public void addAttribute(String name, Object value) {
      values.put(name, value);
   }

   @Override
   public Object getAttribute(String name) {
      Object value = model.getAttribute(name);
      
      if(value == null) {
         return values.get(name);
      }
      return value;
   }
   
}