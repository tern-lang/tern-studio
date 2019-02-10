package tern.studio.common.find;

import java.util.List;

public abstract class MatchEvaluator {
   
   public static MatchEvaluator of(MatchType type, String expression, boolean sensitive) {
      if(type == MatchType.REGEX) {
         return new PatternMatchEvaluator(expression, sensitive);
      }
      return new LiteralMatchEvaluator(expression, sensitive);
   }
   
   public static final String FOREGROUND_COLOR = "#ffffff";
   public static final String BACKGROUND_COLOR = "#6495ed"; 
   
   protected final StringBuilder builder;
   protected final String background;
   protected final String foreground;
   protected final String expression;
   protected final boolean sensitive;
   protected final boolean bold;
   
   MatchEvaluator(String expression, boolean sensitive) {
      this(expression, sensitive, BACKGROUND_COLOR, FOREGROUND_COLOR, true);
   }
   
   MatchEvaluator(String expression, boolean sensitive, String background, String foreground, boolean bold) {
      this.builder = new StringBuilder();
      this.background = background;
      this.foreground = foreground;
      this.expression = expression;
      this.sensitive = sensitive;
      this.bold = bold;
   }
   
   private boolean isCaseSensitive() {
      return sensitive;
   }

   public String match(String line) {
      if(!isCaseSensitive()) {
         String source = line.toLowerCase();
         String token = expression.toLowerCase();
         List<MatchPart> tokens = match(line, source, expression, token);
         
         if(tokens != null) {
            return highlightText(builder, tokens);
         }
         return null;
      }
      List<MatchPart> tokens = match(line, line, expression, expression);
      
      if(tokens != null) {
         return highlightText(builder, tokens);
      }
      return null;
   }
   
   public String replace(String line, String replace) {
      if(!isCaseSensitive()) {
         String source = line.toLowerCase();
         String token = expression.toLowerCase();
         List<MatchPart> tokens = match(line, source, expression, token);
         
         if(tokens != null) {
            return replaceText(builder, replace, tokens);
         }
         return null;
      }
      List<MatchPart> tokens = match(line, line, expression, expression);
      
      if(tokens != null) {
         return replaceText(builder, replace, tokens);
      }
      return null;
   }
   
   private String replaceText(StringBuilder builder, String replace, List<MatchPart> list) {
      for(MatchPart part : list) {
         String begin = part.getBegin();
         String text = part.getMatch();
         
         builder.append(begin);
         
         if(text != null) {
            builder.append(replace);
         }
      }
      String text = builder.toString();
      builder.setLength(0);
      return text;
   }
   
   private String highlightText(StringBuilder builder, List<MatchPart> list) {
      for(MatchPart part : list) {
         String begin = part.getBegin();
         String text = part.getMatch();
         
         builder.append(escape(begin));
         
         if(text != null) {
            builder.append("<span style='background-color: ");
            builder.append(background);
            builder.append("; color: ");
            builder.append(foreground);
            builder.append("; font-weight: ");
            builder.append(bold ? "bold" : "normal");
            builder.append(";'>");
            builder.append(escape(text));
            builder.append("</span>");
         }
      }
      String text = builder.toString();
      builder.setLength(0);
      return text;
   }
   
   private static String escape(String token) {
      return token
            .replace("<", "&lt;")
            .replace(">", "&gt;");
   }
   
   protected abstract List<MatchPart> match(String line, String source, String expression, String token);
}