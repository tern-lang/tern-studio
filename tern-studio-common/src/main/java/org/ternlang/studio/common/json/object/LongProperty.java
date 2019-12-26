package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;

class LongProperty implements Property {

   private final LongValue value;
   private final Field field;
   private final String entity;
   private final String name;
   
   public LongProperty(Field field, Class type, String name){
      this.value = new LongValue(field);
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
         long number = value.toLong();
         field.setLong(source, number);
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
      return long.class;
   }
   
   private static class LongValue extends Value {

      private Object source;
      private Field field;

      public LongValue(Field field) {
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
      public long toLong() {
         try {
            return field.getLong(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
      }
      
      @Override
      public double toDouble() {
         return (double)toLong();
      }
      
      @Override
      public float toFloat() {
         return (float)toLong();
      }
      
      @Override
      public int toInteger() {
         return (int)toLong();
      }
      
      @Override
      public short toShort() {
         return (short)toLong();
      }
      
      @Override
      public byte toByte() {
         return (byte)toLong();
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