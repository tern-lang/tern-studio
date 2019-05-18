package org.ternlang.studio.resource.action.build;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodMatcher {

   private final Class<? extends Annotation> verb;
   private final PathParser expression;
   private final String ignore;

   public MethodMatcher(Class<? extends Annotation> verb, String ignore, String... path) {
      this.expression = new PathParser(path);
      this.ignore = ignore;
      this.verb = verb;
   }

   public Map<String, String> evaluate(String path) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();

      if (!path.isEmpty()) {
         String source = expression.pattern();
         Pattern pattern = Pattern.compile(source);
         Matcher matcher = pattern.matcher(path);

         if (matcher.matches()) {
            List<String> names = expression.names();
            int groups = matcher.groupCount();
            int required = names.size();

            if (groups != required) {
               throw new IllegalStateException("Could not extract parameters from " + path);
            }
            for (int i = 0; i < required; i++) {
               String name = names.get(i);
               String token = matcher.group(i + 1);

               parameters.put(name, token);
            }
         }
      }
      return parameters;
   }
   
   public String verb() {
      return verb.getSimpleName();
   }
   
   public String pattern() {
      return expression.pattern();
   }
   
   public String ignore() {
      return ignore;
   }

   private static class PathParser {

      private List<PathSegment> segments;
      private StringBuilder builder;
      private PathSegment segment;
      private String[] parts;

      public PathParser(String... parts) {
         this.segments = new LinkedList<PathSegment>();
         this.builder = new StringBuilder();
         this.parse(parts);
      }

      public List<String> names() {
         List<String> list = new LinkedList<String>();

         for (PathSegment segment : segments) {
            List<String> names = segment.names();
            list.addAll(names);
         }
         return list;
      }

      public String pattern() {
         StringBuilder builder = new StringBuilder();

         for (PathSegment segment : segments) {
            String pattern = segment.pattern();

            builder.append("/");
            builder.append(pattern);
         }
         return builder.toString();
      }

      public void parse(String... parts) {
         String path = join(parts);
         char[] data = path.toCharArray();

         for (int i = 0; i < data.length; i++) {
            if (data[i] == '{') {
               createToken(TokenType.NORMAL);

               for (i += 1; i < data.length; i++) {
                  char next = data[i];

                  if (next == '}') {
                     break;
                  }
                  append(next);
               }
               createToken(TokenType.PARAMETER);
            } else if (data[i] == '/') {
               createToken(TokenType.NORMAL);
               startSegment();
            } else {
               append(data[i]);
            }
         }
         createToken(TokenType.NORMAL);
         finishPath();
      }

      private String join(String[] parts) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (!part.startsWith("/")) {
               builder.append("/");
            }
            if (i + 1 < parts.length) {
               int length = part.length();

               if (part.endsWith("/")) {
                  part = part.substring(0, length - 1);
               }
            }
            builder.append(part);
         }
         return builder.toString();
      }

      public void append(char ch) {
         builder.append(ch);
      }

      public void append(String text) {
         builder.append(text);
      }

      public void startSegment() {
         if (segment != null) {
            segments.add(segment);
         }
         segment = new PathSegment();
      }

      public void createToken(TokenType type) {
         String value = builder.toString();

         if (segment != null) {
            segment.add(type, value);
         }
         builder.setLength(0);
      }

      public void finishPath() {
         if (segment != null) {
            segments.add(segment);
         }
      }
   }

   private static class PathSegment {

      private final List<Token> tokens;

      public PathSegment() {
         this.tokens = new LinkedList<Token>();
      }

      public List<String> names() {
         List<String> list = new LinkedList<String>();

         for (Token token : tokens) {
            String name = token.name();

            if (name != null) {
               list.add(name);
            }
         }
         return list;
      }

      public String pattern() {
         StringBuilder builder = new StringBuilder();

         for (Token token : tokens) {
            String pattern = token.pattern();
            builder.append(pattern);
         }
         return builder.toString();
      }

      public void add(TokenType type, String text) {
         Token token = new Token(type, text);

         if (text != null) {
            tokens.add(token);
         }
      }
   }

   private static enum TokenType {
      NORMAL, PARAMETER;
   }

   private static class Token {

      public final TokenType type;
      public final String text;

      public Token(TokenType type, String text) {
         this.type = type;
         this.text = text;
      }

      public String name() {
         if (type == TokenType.PARAMETER) {
            return text;
         }
         return null;
      }

      public String pattern() {
         if (type == TokenType.PARAMETER) {
            return "(.+?)";
         }
         return text;
      }
   }
}
