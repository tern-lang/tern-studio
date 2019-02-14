package org.ternlang.studio.index.expression;

public class NumberExpressionArgument implements ExpressionArgument {

   private String value;
   private Number cache;
   
   public NumberExpressionArgument(String value) {
      this.value = value;
   }
   
   @Override
   public boolean isExpression() {
      return false;
   }
   
   @Override
   public Object getValue() {
      if(cache == null) {
         if(value.startsWith("0x") || value.startsWith("0X")) {
            cache = Integer.decode(value);
         } else if(value.endsWith("f") || value.endsWith("F")) {
            cache = new Float(value);
         } else if(value.endsWith("d") || value.endsWith("D")) {
            return new Double(value);
         } else if(value.endsWith("l") || value.endsWith("L")) {
            int length = value.length();
            String number = value.substring(0, length -1);
            cache = new Long(number);
         } else {
            cache = new Integer(value);
         }
      }
      return cache;
   }

   @Override
   public Expression getExpression() {
      return null;
   }
   
   @Override
   public String toString() {
      return value;
   }
}