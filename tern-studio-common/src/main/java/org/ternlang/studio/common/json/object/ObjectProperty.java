package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ObjectProperty implements Property {

   private final PropertyConverter converter;
   private final ObjectValue value;
   private final Field field;
   private final String entity;
   private final String name;
   private final Class type;
   private final boolean primitive;
   private final boolean array;
   
   public ObjectProperty(PropertyConverter converter, Field field, Class type, String name, boolean primitive, boolean array){
      this.value = new ObjectValue(field);
      this.entity = type.getSimpleName();
      this.converter = converter;
      this.primitive = primitive;
      this.array = array;
      this.field = field;
      this.type = type;
      this.name = name;
   }

   @Override
   public boolean isArray() {
      return array;
   }

   @Override
   public boolean isPrimitive() {
      return primitive;
   }

   @Override
   public Value getValue(Object source) {
      try {
         return value.with(source);
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
   public String getEntity() {
      return entity;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public Class getType() {
      return type;
   }
   
   private static class ObjectValue extends Value {

      private Object source;
      private Field field;

      public ObjectValue(Field field) {
         this.field = field;
      }
      
      public Value with(Object source) {
         this.source = source;
         return this;
      }
      
      @Override
      public Object toObject() {
         try {
            return field.get(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
      }

      @Override
      public CharSequence toText() {
         Object object = toObject();
         return String.valueOf(object);
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public void reset() {
         source = null;
      }
   }
}