package org.ternlang.studio.common.find;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatchEvaluator extends MatchEvaluator {
   
   private final Pattern pattern;

   PatternMatchEvaluator(String expression, boolean sensitive) {
      super(expression, sensitive);
      this.pattern = Pattern.compile(expression, sensitive ? 0 : Pattern.CASE_INSENSITIVE);
   }

   PatternMatchEvaluator(String expression, boolean sensitive, String background, String foreground, boolean bold) {
      super(expression, sensitive, background, foreground, bold);
      this.pattern = Pattern.compile(expression, sensitive ? 0 : Pattern.CASE_INSENSITIVE);
   }

   @Override
   protected List<MatchPart> match(String line, String source, String expression, String token) {
      Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
         List<MatchPart> tokens = new ArrayList<MatchPart>();
         int start = matcher.start();
         int end = matcher.end();
         String begin = line.substring(0, start);
         String text = line.substring(start, end);
         tokens.add(new MatchPart(begin, text));
         int last = end;

         while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            begin = line.substring(last, start);
            text = line.substring(start, end);
            tokens.add(new MatchPart(begin, text));
            last = end;
         }
         int length = line.length();
         String remainder = line.substring(last, length);
         tokens.add(new MatchPart(remainder, null));
         return tokens;
      }
      return null;

   }
}