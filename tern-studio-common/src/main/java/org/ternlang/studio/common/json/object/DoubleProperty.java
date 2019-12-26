package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;

class DoubleProperty implements Property {

   private final DoubleValue value;
   private final Field field;
   private final String entity;
   private final String name;
   
   public DoubleProperty(Field field, Class type, String name){
      this.value = new DoubleValue(field);
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
         double number = value.toDouble();
         field.setDouble(source, number);
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
      return double.class;
   }
   
   private static class DoubleValue extends Value {

      private Object source;
      private Field field;

      public DoubleValue(Field field) {
         this.field = field;
      }
      
      public Value with(Object source) {
         this.source = source;
         return this;
      }
      
      @Override
      public CharSequence toText() {
         Object object = toObject();
         return String.valueOf(object);
      }
      
      @Override
      public boolean toBoolean() {
         throw new IllegalStateException("Could not convert to boolean");
      }
      
      @Override
      public char toCharacter() {
         throw new IllegalStateException("Could not convert to character");
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
      public double toDouble() {
         try {
            return field.getDouble(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
      }
      
      @Override
      public float toFloat() {
         return (float)toDouble();
      }
      
      @Override
      public long toLong() {
         return (long)toDouble();
      }
      
      @Override
      public int toInteger() {
         return (int)toInteger();
      }
      
      @Override
      public short toShort() {
         return (short)toDouble();
      }
      
      @Override
      public byte toByte() {
         return (byte)toDouble();
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