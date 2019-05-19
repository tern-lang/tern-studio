package org.ternlang.studio.core.complete;

import org.ternlang.studio.common.PatternEscaper;

public class SearchExpressionParser {

   private static final String STAR_PATTERN = "_STAR_PATTERN_";
   
   public static String parse(String expression) {      
      if(expression != null && !expression.isEmpty()) {
         expression = expression.replace("*", STAR_PATTERN);
         expression = PatternEscaper.escape(expression);
         expression = expression.replace(STAR_PATTERN, ".*");
         return expression + ".*";
      }
      return ".*";
   }
}
