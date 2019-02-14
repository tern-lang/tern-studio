package org.ternlang.studio.index.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ternlang.parse.StringParser;

public class ExpressionParser extends StringParser implements Expression {
   
   private final List<ExpressionToken> tokens;
   private final TokenList arguments;
   private final BraceStack braces;
   private final QuoteStack quotes;
   private final Token name;
   
   public ExpressionParser() {
      this.tokens = new ArrayList<ExpressionToken>();
      this.arguments = new TokenList();
      this.quotes = new QuoteStack();
      this.braces = new BraceStack();
      this.name = new Token();
   }

   public ExpressionParser(String text) {
      this();
      parse(text);
   }
   
   public String getExpression() {
      return new String(source, 0, count);
   }
   
   @Override
   public List<ExpressionToken> getTokens() {
      return Collections.unmodifiableList(tokens);
   }

   @Override
   protected void init() {
      tokens.clear();
      name.clear();
      braces.clear();
      quotes.clear();
   }

   @Override
   protected void parse() {
      pack();
      expression();
   }
   
   private void pack() {
      int pos = 0;

      while(off < count){
         if(quote(source[off])){ 
            char open = source[off];
            
            while(off < count) {
               source[pos++] = source[off++];

               if(source[off] == open) {
                  source[pos++] = source[off++];
                  break;
               }
            }
         } else if(!space(source[off])) {
            source[pos++] = source[off++];
         } else {
            off++;
         }
      }
      count = pos;
      off = 0;
   }
   
   private void expression() {
      while(off < count) {
         reference();
         insert();
         reset();
      }
      wild();
   }
   
   private void wild() {
      name.off = 0;
      name.len = 0;
      
      if(count > 0) {
         char last = source[count-1];
         
         if(point(last)) {
            insert();
         }
      } else {
         insert();
      }
   }
   
   private void reference() {
      name.off = off;
      
      while(off < count) {
         char next = source[off++];
         
         if(braces.open(next)) {
            argument();
            break;
         }
         if(terminal(next)) {
            break;
         }
         name.len++;
      }
   }
   
   private void argument() {  
      int mark = off;
      int len = 0;
      
      while(off < count) {
         char next = source[off++];

         if(quotes.isOpen()) {
            if(quotes.close(next) && quotes.isEmpty()) {
               if(braces.isCurrent()) {
                  if(len > 0) {
                     arguments.add(mark, len -1, TokenType.QUOTE);
                     len = 0;
                  }
               }
            } 
         } else {
            if(braces.isEmpty() && terminal(next)) {
               break;
            } else if(braces.close(next) && braces.isEmpty()) { // blah.invoke( blah.invoke(), blah.invoke( a, b ) [)]
               if(len > 0) {
                  char prev = source[off - 2];
                  
                  if(!quote(prev)) {
                     arguments.add(mark, len);
                  }
                  len = 0;
               }
            } else if(braces.isCurrent()) { // blah.invoke( blah.invoke()[,] blah.invoke( a, b ) [)]
               if(separator(next)) {  // blah.invoke( blah.invoke()[,] blah.invoke() [)]
                  char prev = source[off - 2];
                  
                  if(!quote(prev)) {
                     arguments.add(mark, len);
                  }
                  mark = off;
                  len = -1;
               } 
               if(digit(next) || minus(next)) {
                  char prev = source[off - 2];
                  
                  if(separator(prev) || openBracket(prev)) { // invoke( 12.0f ).call( text.list[12])
                     while(off < count) {
                        char curr = source[off++];
                        
                        if(!digit(curr) && !point(curr)) { // 12.112
                           if(terminal(curr) || separator(curr) || closeBracket(curr)) { // x.call(11) || x[11] || 11;
                              arguments.add(mark, len +1, TokenType.NUMBER);
                              mark = off;
                              len = -1;
                              break;
                           } 
                        }
                        len++;
                     }
                  }
               }
            } 
            if(quotes.isEmpty() && quotes.open(next)) {
               if(braces.isCurrent()) {
                  mark = off;
                  len = 0;
               }
            }
            braces.open(next);
         }
         len++;
      }
   }
   
   private void reset() {
      name.clear();
      arguments.clear();
      braces.clear();
      quotes.clear();
   }
   
   private void insert() {
      String key = name.toString();
      ExpressionBraceType type = braces.braces();
      
      insert(key, type);
   }
   
   private void insert(String name, ExpressionBraceType brace) {
      ExpressionArgument[] list = new ExpressionArgument[arguments.count];
      
      for(int i = 0; i < list.length; i++) {
         String value = arguments.list[i].toString();
         TokenType type = arguments.list[i].getType();
         
         list[i] = type.createArgument(value);
      }      
      ExpressionToken token = new ExpressionToken(name, list, brace);
      tokens.add(token);
   } 
   
   private boolean point(char ch) {
      return ch == '.';
   }
   
   private boolean minus(char ch) {
      return ch == '-';
   }
   
   private boolean openBracket(char ch) {
      return ch == '{' || ch == '(' || ch == '[';
   }
   
   private boolean closeBracket(char ch) {
      return ch == '}' || ch == ')' || ch == ']';
   }
   
   private boolean separator(char ch) {
      return ch == ',';
   }
   
   private boolean terminal(char ch) {
      return ch == '.' || ch == ';';
   }
   
   @Override
   public String toString() {
      return getExpression();
   }
   
   private class QuoteStack {
      
      private char[] stack;
      private int count;
      
      public QuoteStack() {
         this.stack = new char[16];
      }
      
      public boolean open(char ch) {
         if(ch == '"') {
            stack[count++] = '"';
            return true;
         }
         if(ch == '\'') {
            stack[count++] = '\'';
            return true;
         }
         return false;
      }
      
      public boolean close(char ch) {
         if(count > 0) {
            if(stack[count -1] == ch) {
               count--;
               return true;
            }
         }
         return false;
      }
      
      public boolean isOpen() {
         return count > 0;
      }
      
      public boolean isEmpty() {
         return count == 0;
      }
      
      public void clear() {
         stack[0] = 0;
         count = 0;
      }
   }
   
   private enum TokenType {
      QUOTE{
         public ExpressionArgument createArgument(String value) {
            return new QuoteExpressionArgument(value);
         }
      },
      EXPRESSION {
         public ExpressionArgument createArgument(String value) {
            if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
               return new BooleanExpressionArgument(value);
            }
            return new SubExpressionArgument(value);
         }
      },
      NUMBER{
         public ExpressionArgument createArgument(String value) {
            return new NumberExpressionArgument(value);
         }
      },
      BOOLEAN{
         public ExpressionArgument createArgument(String value) {
            return new BooleanExpressionArgument(value);
         }
      };
      
      public abstract ExpressionArgument createArgument(String value);
   }
   
   private class BraceStack {
      
      private char[] stack;
      private int count;
      
      public BraceStack() {
         this.stack = new char[16];
      }
      
      public ExpressionBraceType braces() {
         if(stack[0] == ']') {
            return ExpressionBraceType.INDEX;
         }
         if(stack[0] == ')') {
            return ExpressionBraceType.INVOKE;
         }
         if(stack[0] == '}') {
            return ExpressionBraceType.ENCLOSE;
         }
         return null;
      }
      
      public boolean open(char ch) {
         if(ch == '[') {
            stack[count++] = ']';
            return true;
         }
         if(ch == '(') {
            stack[count++] = ')';
            return true;
         }
         if(ch == '{') {
            stack[count++] = '}';
            return true;
         }
         return false;
      }
      
      public boolean close(char ch) {
         if(count > 0) {
            if(stack[count - 1] == ch) {
               count--;
               return true;
            }
         }
         return false;
      }
      
      public boolean isCurrent() {
         return count == 1;
      }
      
      public boolean isEmpty() {
         return count == 0;
      }
      
      public void clear() {
         stack[0] = 0;
         count = 0;
      }
   }
   
   private class Token {

      public TokenType type;
      public String cache;
      public int off;
      public int len;
      
      public Token() {
         this(0, 0, TokenType.EXPRESSION);
      }
      
      public Token(int off, int len, TokenType type) {
         this.type = type;
         this.off = off;
         this.len = len;
      }

      public TokenType getType() {
         return type;
      }
      
      public void clear() {
         type = null;
         cache = null;
         len = 0;
      }
      
      public String toString() {
         if(cache != null) {
            return cache;
         }
         if(len >= 0) {
            cache = new String(source,off,len);
         }
         return cache;
      }
   }
   
   private class TokenList {
      
      public Token[] list;
      public int count;

      public TokenList(){
         list = new Token[20];
      }
      
      public void add(int off, int len){
         add(off, len, TokenType.EXPRESSION);
      }
      
      public void add(int off, int len, TokenType type){
         if(count+1 > list.length) {
            resize(count *2);
         }
         list[count++] = new Token(off, len ,type);
      }

      public void clear(){
         count =0;
      }
   
      private void resize(int size){
         Token[] copy = new Token[size];
         System.arraycopy(list,0,copy,0,count);
         list = copy; 
      }
   }
}
