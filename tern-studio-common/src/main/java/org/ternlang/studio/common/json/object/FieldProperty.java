package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class FieldProperty implements Property {

   private final PropertyConverter converter;
   private final Field field;
   private final String name;
   private final Class type;
   private final boolean array;
   
   public FieldProperty(PropertyConverter converter, Field field){
      this.type = field.getType();
      this.name = type.getSimpleName();
      this.array = type.isArray();
      this.converter = converter;
      this.field = field;
   }

   @Override
   public Object getValue(Object source) {
      try {
         return field.get(source);
      } catch(Exception e) {
         throw new IllegalStateException("Illegal access to " + field, e);
      }
   }
  
   @Override
   public void setValue(Object source, Object value) {
      try {
         field.set(source, value);
      } catch(Exception e) {
         throw new IllegalStateException("Illegal access to " + field, e);
      }
   }
      
   @Override
   public void setValue(Object source, Value value) {
      try {
         if (!array) {
            Object converted = converter.convert(type, value);
            field.set(source, converted);
         }
      } catch(Exception e) {
         throw new IllegalStateException("Illegal access to " + field, e);
      }
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public Class getType() {
      return type;
   }
}