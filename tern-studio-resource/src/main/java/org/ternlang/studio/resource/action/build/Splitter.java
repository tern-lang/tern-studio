package org.ternlang.studio.resource.action.build;

abstract class Splitter {

   protected StringBuilder builder;
   protected char[] text;
   protected int count;
   protected int off;
   
   public Splitter(String source) {
      this.builder = new StringBuilder();
      this.text = source.toCharArray();
      this.count = text.length;
   }
   
   public String process() {
      while(off < count) {
         while(off < count) {
            char ch = text[off];
            
            if(!isSpecial(ch)) {
               break;
            }
            builder.append(ch);
            off++;
         }
         if(!acronym()) {
            token();
            number();
         }
      }
      return builder.toString();
   }
   
   private void token() {
      int mark = off;
      
      while(mark < count) {
         char ch = text[mark];
         
         if(!isLetter(ch)) {
            break;
         } 
         if(mark > off) {
            if(isUpper(ch)) {
               break;
            }
         }
         mark++;
      }
      if(mark > off) {
         parse(text, off, mark - off);
         commit(text, off, mark - off);
      }
      off = mark;
   }
   
   private boolean acronym() { // is it the last one?
      int mark = off;
      int size = 0;
      
      while(mark < count) {
         char ch = text[mark];
         
         if(isUpper(ch)) {
            size++;
         } else {
            break;
         }
         mark++;
      }
      if(size > 1) {
         if(mark < count) {
            char ch = text[mark-1];
            
            if(isUpper(ch)) {
               mark--;
            }
         }
         commit(text, off, mark - off);
         off = mark;
      }
      return size > 1;
   }
   
   private boolean number() {
      int mark = off;
      int size = 0;
      
      while(mark < count) {
         char ch = text[mark];
         
         if(isDigit(ch)) {
            size++;
         } else {
            break;
         }
         mark++;
      }
      if(size > 0) {
         commit(text, off, mark - off);
      }
      off = mark;
      return size > 0;
   }

   protected boolean isLetter(char ch) {
      return Character.isLetter(ch);
   }
   
   protected boolean isSpecial(char ch) {
      return !Character.isLetterOrDigit(ch);
   }
   
   protected boolean isDigit(char ch) {
      return Character.isDigit(ch);
   }
   
   private boolean isUpper(char ch) {
      return Character.isUpperCase(ch);
   }
   
   protected char toUpper(char ch) {
      return Character.toUpperCase(ch);
   }
   
   protected char toLower(char ch) {
      return Character.toLowerCase(ch);
   }
   
   protected abstract void parse(char[] text, int off, int len);
   protected abstract void commit(char[] text, int off, int len);
}
