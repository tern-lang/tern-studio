package org.ternlang.studio.common;

public class PatternEscaper {

   public static String escape(String expression) {
      while(expression.contains("\\\\")) { // ensure \\ does not exist
         expression = expression.replace("\\\\", "\\");
      }
      while(expression.contains("//")) { // ensure // does not exist
         expression = expression.replace("//", "/");
      }
      expression = expression.replace("\\", "\\\\"); // escape \
      expression = expression.replace(".", "\\."); // escape .
      expression = expression.replace("(", "\\("); // escape (
      expression = expression.replace(")", "\\)"); // escape )
      expression = expression.replace("-", "\\-"); // escape -
      expression = expression.replace("$", "\\$"); // escape $
      expression = expression.replace("*", "\\*"); // escape *
      expression = expression.replace("+", "\\+"); // escape +
      
      return expression;
   }
}