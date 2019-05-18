package org.ternlang.studio.resource.template;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ternlang.studio.resource.Content;

public class StringTemplate implements Template {

   private final TokenIterator iterator;
   private final List<Token> tokens;
   private final Content content;
   private final String path;
   private final long time;
   
   public StringTemplate(Content content, String path, String template, long time) {
      this.iterator = new TokenIterator(template);
      this.tokens = new ArrayList<Token>();
      this.content = content;
      this.path = path;
      this.time = time;
   }

   @Override
   public void render(TemplateFilter filter, Writer writer) throws Exception {
      while(iterator.hasNext()) {
         Token token = iterator.next();
         
         if(token != null) {
            tokens.add(token);  
         }
      }
      int length = tokens.size();
      
      for(int i = 0; i < length; i++) {
         Token token = tokens.get(i);
      
         if(token != null) {
            token.process(filter, writer);
         }
      }
   }
   
   @Override
   public boolean isStale() {
      return time < content.getModificationTime();
   }
   
   private class TokenIterator {
      
      private char[] source;
      private int off;
      
      public TokenIterator(String template) {
         this.source = template.toCharArray();
      }
      
      public Token next() {
         int mark = off;
         
         while(off < source.length){
            char next = source[off];

            if(next == '$') {
               if(off > mark) {
                  return new TextToken(source, mark, off - mark);
               }
            } else if(off > 0) {
               char prev = source[off - 1];
               
               if(next == '{' && prev == '$') {
                  while(off < source.length) {
                     if(source[off++] == '}') {
                        return new VariableToken(source, mark, off - mark);
                     }
                  }
               }
            }
            off++;
         }
         if(off > mark) {
            return new TextToken(source, mark, off - mark);
         }
         return null;
      } 
      
      public boolean hasNext() {
         return off < source.length;
      }
   }
   
   private interface Token {
      void process(TemplateFilter processor, Writer writer) throws Exception; 
   }
   
   private class TextToken implements Token {
      
      private char[] source;
      private int off;
      private int length;
      
      public TextToken(char[] source, int off, int length) {
         this.source = source;
         this.length = length;
         this.off = off;         
      }
      
      @Override
      public void process(TemplateFilter processor, Writer writer) throws Exception {
         writer.write(source, off, length);
      } 
      
      @Override
      public String toString() {
         return new String(source, off, length);
      }
   }
      
   private class VariableToken implements Token {
      
      private String variable;
      private char[] source;
      private int off;
      private int length;
      
      public VariableToken(char[] source, int off, int length) {
         this.variable = new String(source, off + 2, length - 3);
         this.source = source;
         this.length = length;
         this.off = off;         
      }
      
      @Override
      public void process(TemplateFilter processor, Writer writer) throws Exception {
         Object value = processor.process(variable);

         if(value == null) {
            writer.write(source, off, length);
         } else {
            String text = String.valueOf(value);
            writer.append(text);            
         }
      }   
      
      @Override
      public String toString() {
         return new String(source, off, length);
      }
   }
}