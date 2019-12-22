package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.document.Value;

public class ValueConverter {

   public Object convert(Class type, Value value) {
      Class actual = convert(type);

      if(actual == String.class) {
         return value.toString();
      }
      if(actual == Integer.class) {
         return value.toInteger();
      }
      if(actual == Double.class) {
         return value.toDouble();
      }
      if(actual == Float.class) {
         return value.toFloat();
      }
      if(actual == Boolean.class) {
         return value.toBoolean();
      }
      if(actual == Byte.class) {
         return value.toBoolean();
      }
      if(actual == Short.class) {
         return value.toShort();
      }
      if(actual == Long.class) {
         return value.toLong();
      }
      if(actual == Character.class) {
         return value.toCharacter();
      }
      throw new IllegalStateException("Unable to convert " + type);
   }

   public boolean accept(Class type) {
      Class actual = convert(type);

      if(actual == String.class) {
         return true;
      }
      if(actual == Integer.class) {
         return true;
      }
      if(actual == Double.class) {
         return true;
      }
      if(actual == Float.class) {
         return true;
      }
      if(actual == Boolean.class) {
         return true;
      }
      if(actual == Byte.class) {
         return true;
      }
      if(actual == Short.class) {
         return true;
      }
      if(actual == Long.class) {
         return true;
      }
      if(actual == Character.class) {
         return true;
      }
      return false;
   }

   public Class convert(Class type) {
      if(type == String.class) {
         return String.class;
      }
      if(type == int.class) {
         return Integer.class;
      }
      if(type == double.class) {
         return Double.class;
      }
      if(type == float.class) {
         return Float.class;
      }
      if(type == boolean.class) {
         return Boolean.class;
      }
      if(type == byte.class) {
         return Byte.class;
      }
      if(type == short.class) {
         return Short.class;
      }
      if(type == long.class) {
         return Long.class;
      }
      if(type == char.class) {
         return Character.class;
      }
      return type;
   }

   public Object box(Class type) {
      if(type == int.class) {
         return 0;
      }
      if(type == double.class) {
         return 0.0;
      }
      if(type == float.class) {
         return 0f;
      }
      if(type == boolean.class) {
         return false;
      }
      if(type == byte.class) {
         return (byte) 0x00;
      }
      if(type == short.class) {
         return (short) 0;
      }
      if(type == long.class) {
         return 0L;
      }
      if(type == char.class) {
         return (char) 0;
      }
      return null;
   }
}
