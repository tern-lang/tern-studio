package tern.studio.index.complete;

import static tern.studio.index.expression.ExpressionBraceType.isCloseBrace;
import static tern.studio.index.expression.ExpressionBraceType.isOpenBrace;
import static tern.studio.index.expression.ExpressionBraceType.resolveBraceType;

import tern.common.ArrayStack;
import tern.common.Stack;
import tern.studio.index.expression.ExpressionBraceType;

public class InputExpressionParser {

   public static String parseLine(String source, int index) { // for testing
      String lines[] = source.split("\\r?\\n");
      String expression = parseLine(lines, index);
      
      return expression.trim();
   }
   
   public static String parseLine(String[] lines, int index) {
      Stack<ExpressionBraceType> braces = new ArrayStack<ExpressionBraceType>();
      Stack<Character> quotes = new ArrayStack<Character>();
      StringBuilder builder = new StringBuilder();
      
      if(lines.length >= index) {
         for(int i = index; i > 0; i--) {
            String trimmed = lines[i - 1].trim(); // lines start at 1
            int length = trimmed.length();
            int begin = length -1;
            
            while(begin >= 0) {
               char next = trimmed.charAt(begin);
               
               if(!quotes.isEmpty()) {
                  if(isQuote(next)) {
                     char top = quotes.peek();
                     
                     if(top == next) { // have we closed the quotes
                        int seek = begin -1;
                        int escape = 0;
                        
                        while(seek >= 0) {
                           char previous = trimmed.charAt(seek);
                           
                           if(previous != '\'') {
                              break;
                           }
                           seek--;
                        }
                        if(escape % 2 == 0) { // there was even or no braces
                           quotes.pop();
                        }
                     } 
                  }
               } else {
                  if(isQuote(next)) {
                     quotes.push(next);
                  } else if(isCloseBrace(next)) {
                     ExpressionBraceType type = resolveBraceType(next);

                     if(isTerminal(next)) { 
                        int depth = braces.size();
                        
                        if(depth == 0) {
                           return builder.toString();
                        }
                     }
                     braces.push(type);
                  } else if(isOpenBrace(next)) {
                     int size = builder.length();
                     int depth = braces.size();
                     
                     if(depth == 0) { // no braces in stack
                        if(size > 0 || i != index) { // we have something or new lines
                           return builder.toString();
                        }
                     } else {
                        ExpressionBraceType top = braces.peek();
                        
                        if(top.open != next) {
                           return builder.toString();
                        }
                        braces.pop(); // remove brace
                     }
                  } else if(isTerminal(next)) {
                     int depth = braces.size();
                     
                     if(isSafeNavigation(next)) { // [?].blah
                        int done = builder.length();
                        
                        if(done > 0) {
                           char previous = builder.charAt(0); // ?[.]blah
                           
                           if(isNavigation(previous)) {
                              builder.delete(0, 1); // remove '.'
                              depth = 1; // don't finish
                              next = '.';  // insert '.'
                           }
                        } 
                     } 
                     if(depth == 0) {
                        return builder.toString();
                     }
                  }
               }
               if(isAlphaOrDigit(next)) {
                  int depth = braces.size();
                  int done = builder.length();
                  
                  if(done > 0 && depth == 0) {
                     char previous = builder.charAt(0);
                     
                     if(isSpace(previous)) {
                        for(int j = 0; j < done; j++) {
                           char seek = builder.charAt(j);
                           
                           if(!isSpace(seek)) {
                              if(isAlpha(seek)) {
                                 builder.delete(0, j);
                                 return builder.toString();
                              }
                           }
                        }
                     }
                  }
               }
               builder.insert(0, next);
               begin--;
            }
            lines[i - 1] = ""; // clear the expression
         }
      }
      return builder.toString();
   }

   private static boolean isAlpha(char value) {
      return Character.isAlphabetic(value);
   }
   
   private static boolean isAlphaOrDigit(char value) {
      return Character.isDigit(value) || Character.isAlphabetic(value);
   }

   public static boolean isNavigation(char value) {
      return value == '.';
   }
   
   public static boolean isSafeNavigation(char value) {
      return value == '?';
   }
   
   private static boolean isSpace(char value) {
      switch(value){
      case ' ': case '\t':
      case '\r': case '\n':
         return true;
      }
      return false;
   }
   
   private static boolean isQuote(char value) {
      switch(value){
      case '"': case '\'':
      case '`':
         return true;
      }
      return false;
   }
   
   private static boolean isTerminal(char value) {
      switch(value) {
      case ',': case '{':
      case '(': case '+':
      case '-': case '*':
      case '/': case '%':
      case '|': case '&':
      case '?': case ':':
      case '=': case '<':
      case '>': case ';':
      case '}':   
         return true;
      }
      return false;
   }
}
