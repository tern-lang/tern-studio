package org.ternlang.studio.service.complete;

import org.simpleframework.http.Request;
import org.ternlang.studio.common.PatternEscaper;

public class SearchExpressionParser {

   private static final String STAR_PATTERN = "_STAR_PATTERN_";
   private static final String EXPRESSION = "expression";
   
   public static String parse(Request request) {      
      String expression = request.getParameter(EXPRESSION);
      
      if(expression != null && !expression.isEmpty()) {
         expression = expression.replace("*", STAR_PATTERN);
         expression = PatternEscaper.escape(expression);
         expression = expression.replace(STAR_PATTERN, ".*");
         return expression + ".*";
      }
      return ".*";
   }
}
