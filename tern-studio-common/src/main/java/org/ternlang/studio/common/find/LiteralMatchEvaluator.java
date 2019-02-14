package org.ternlang.studio.common.find;

import java.util.ArrayList;
import java.util.List;

public class LiteralMatchEvaluator extends MatchEvaluator {

   LiteralMatchEvaluator(String expression, boolean sensitive) {
      super(expression, sensitive);
   }

   LiteralMatchEvaluator(String expression, boolean sensitive, String background, String foreground, boolean bold) {
      super(expression, sensitive, background, foreground, bold);
   }
   
   @Override
   protected List<MatchPart> match(String line, String source, String expression, String token) {
      int index = source.indexOf(token);
      
      if(index >= 0) {
         List<MatchPart> tokens = new ArrayList<MatchPart>();
         int length = expression.length();
         int start = 0;
         
         while(index >= 0) {
            String begin = line.substring(start, index);
            String text = line.substring(index, index + length);
            
            tokens.add(new MatchPart(begin, text));
            start = index + length;
            index = source.indexOf(token, start);
         }
         int last = line.length();
         String remainder = line.substring(start, last);
         tokens.add(new MatchPart(remainder, null));
         return tokens;
      }
      return null;
   }
}