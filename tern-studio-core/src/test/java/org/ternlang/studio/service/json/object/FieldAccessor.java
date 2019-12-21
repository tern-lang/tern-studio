package org.ternlang.studio.service.json.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class FieldAccessor  {

   private final Constructor factory;
   private final Field field;
   private final Class type;
   
   public FieldAccessor(Field field, Constructor factory){
      this.type = field.getType();
      this.factory = factory;
      this.field = field;
   }
   
   public Class getType() {
      return type;
   }
   
   public Object getInstance() {
      try {
         return factory.newInstance();
      } catch(Exception e) {
         throw new IllegalStateException("Could not instantiate", e);
      }
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