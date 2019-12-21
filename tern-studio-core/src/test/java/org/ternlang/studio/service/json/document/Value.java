package org.ternlang.studio.service.json.document;

public abstract class Value {

   public double toDouble() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Double.parseDouble(string);
   }
   
   public float toFloat() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Float.parseFloat(string);
   }
   
   public long toLong() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Long.parseLong(string);
   }
   
   public int toInteger() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Integer.parseInt(string);
   }
   
   public int toShort() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Short.parseShort(string);
   }
   
   public int toByte() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Byte.parseByte(string);
   }
   
   public boolean toBoolean() {
      CharSequence text = toText();
      String string = text.toString();
      
      return Boolean.parseBoolean(string);
   }
   
   public char toCharacter() {
      CharSequence text = toText();
      return text.charAt(0);
   }
   
   public boolean isNull() {
      return false;
   }
   
   public abstract CharSequence toText();
   public abstract boolean isEmpty();
   public abstract void reset();
}
