package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;

class BooleanProperty implements Property {

   private final BooleanValue value;
   private final Field field;
   private final String entity;
   private final String name;
   
   public BooleanProperty(Field field, Class type, String name){
      this.value = new BooleanValue(field);
      this.entity = type.getSimpleName();
      this.field = field;
      this.name = name;
   }

   @Override
   public boolean isArray() {
      return false;
   }

   @Override
   public boolean isPrimitive() {
      return true;
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
         boolean bool = value.toBoolean();
         field.setBoolean(source, bool);
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
      return boolean.class;
   }
   
   private static class BooleanValue extends Value {

      private Object source;
      private Field field;

      public BooleanValue(Field field) {
         this.field = field;
      }
      
      public Value with(Object source) {
         this.source = source;
         return this;
      }
      
      @Override
      public CharSequence toText() {
         return toBoolean() ? "true" : "false";
      }

      @Override
      public boolean toBoolean() {
         try {
            return field.getBoolean(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
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
      public short toShort() {
         throw new IllegalStateException("Could not convert to number");
      }
      
      @Override
      public double toDouble() {
         throw new IllegalStateException("Could not convert to number");
      }
      
      @Override
      public float toFloat() {
         throw new IllegalStateException("Could not convert to number");
      }
      
      @Override
      public int toInteger() {
         throw new IllegalStateException("Could not convert to number");
      }
      
      @Override
      public long toLong() {
         throw new IllegalStateException("Could not convert to number");
      }
      
      @Override
      public byte toByte() {
         throw new IllegalStateException("Could not convert to number");
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