package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

public class FieldAttribute  {

   private final Field field;
   private final String name;
   private final Class type;
   
   public FieldAttribute(Field field){
      this.type = field.getType();
      this.name = type.getSimpleName();
      this.field = field;
   }
   
   public String getName() {
      return name;
   }
   
   public Class getType() {
      return type;
   }
   
   public Object getValue(Object source) {
      try {
         return field.get(source);
      } catch(Exception e) {
         throw new IllegalStateException("Illegal access to " + field, e);
      }
   }

   public void setValue(Object source, Object value) {
      try {
         field.set(source, value);
      } catch(Exception e) {
         throw new IllegalStateException("Illegal access to " + field, e);
      }
   }
}