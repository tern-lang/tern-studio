package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.entity.Property;

class CharacterProperty implements Property {

   private final CharacterValue value;
   private final Field field;
   private final String entity;
   private final String name;
   
   public CharacterProperty(Field field, Class type, String name){
      this.value = new CharacterValue(field);
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
         char bool = value.toCharacter();
         field.setChar(source, bool);
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
      return char.class;
   }
   
   private static class CharacterValue extends Value {

      private Object source;
      private Field field;

      public CharacterValue(Field field) {
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
      public Object toObject() {
         try {
            return field.get(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
      }

      @Override
      public char toCharacter() {
         try {
            return field.getChar(source);
         } catch(Exception e) {
            throw new IllegalStateException("Illegal access to " + field, e);
         }
      }
      
      @Override
      public boolean toBoolean() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public short toShort() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public double toDouble() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public float toFloat() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public int toInteger() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public long toLong() {
         throw new IllegalStateException("Could not convert to character");
      }
      
      @Override
      public byte toByte() {
         throw new IllegalStateException("Could not convert to character");
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