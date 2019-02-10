package tern.studio.agent.debug;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import tern.core.ModifierType;

public class ValueData {
   
   public static final String NAME_KEY = "name";
   public static final String TYPE_KEY = "type";
   public static final String VALUE_KEY = "value";
   public static final String DESCRIPTION_KEY = "description";
   public static final String EXPANDABLE_KEY = "expandable";
   public static final String DEPTH_KEY = "depth";
   public static final String MODIFIERS_KEY = "modifiers";
   public static final String PROPERTY_KEY = "property";
   
   private final String name;
   private final String type;
   private final String value;
   private final String description;
   private final boolean expandable;
   private final int modifiers;
   private final int depth;
   
   public ValueData(String name, String type, String value, String description, boolean expandable, int modifiers, int depth) {
      this.name = name;
      this.type = type;
      this.value = value;
      this.modifiers = modifiers;
      this.description = description;
      this.expandable = expandable;
      this.depth = depth;
   }
   
   public Map<String, String> getData() {
      Map<String, String> data = new LinkedHashMap<String, String>();

      data.put(NAME_KEY, name);
      data.put(TYPE_KEY, type);
      data.put(VALUE_KEY, value);
      data.put(DESCRIPTION_KEY, description);
      data.put(MODIFIERS_KEY, getModifiers());    
      data.put(PROPERTY_KEY, String.valueOf(!ModifierType.isDefault(modifiers)));     
      data.put(EXPANDABLE_KEY, String.valueOf(expandable));
      data.put(DEPTH_KEY, String.valueOf(depth));
      
      return Collections.unmodifiableMap(data);
   }
   
   private String getModifiers() {
      StringBuilder builder = new StringBuilder();
      
      if(!ModifierType.isDefault(modifiers)) {
         if(ModifierType.isStatic(modifiers)) {
            builder.append("[static]");
         }
         if(ModifierType.isPrivate(modifiers)) {
            builder.append("[private]");
         }
         if(ModifierType.isProtected(modifiers)) {
            builder.append("[protected]");
         }
         if(ModifierType.isPublic(modifiers)) {
            builder.append("[public]");
         }
         if(ModifierType.isConstant(modifiers)) {
            builder.append("[const]");
         }
         if(ModifierType.isVariable(modifiers)) {
            builder.append("[var]");
         }
      }
      return builder.toString();
   }
   
}