package org.ternlang.studio.service.json;

import java.lang.reflect.Field;

class FieldAccessor {

   private final Field field;
   
   public FieldAccessor(Field field){
      this.field = field;
   }
   
   public Class getType() {
      return field.getType();
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