package org.ternlang.studio.index.complete;

public class CommentStripper {
   
   private char[] original;
   private char[] clean;
   private int write;
   private int read;
   private int count;
   
   public CommentStripper(char[] original) {
      this.clean = new char[original.length];
      this.count = original.length;
      this.original = original;
   }
   
   public String clean() {
      if(read < count) {
         directive(); // read interpreter directive
      }
      while(read < count) {
         char next = original[read];
         
         if(comment(next)) {
            if(!comment()) {
               clean[write++] = original[read++];
            }
         } else if(quote(next)) {
            if(!string()) {
               clean[write++] = original[read++];
            }
         } else  {
            clean[write++] = original[read++];
         } 
      }
      return new String(clean);
   }
   
   private boolean directive() {
      char start = original[read];
      
      if(directive(start)){
         if(read + 1 < count) {   
            char next = original[read + 1];
            
            if(next == '!') {
               while(read < count) {
                  char terminal = original[read++];
                  
                  if(terminal == '\n') {
                     clean[write++] = '\n';
                     return true;
                  }
                  if(clean.length <= write) {
                     throw new IllegalStateException("Buffer overflow for " + clean.length + " at " + write);
                  }
                  clean[write++] = ' ';
               }
               return true; // end of source
            } 
         }
      }
      return false;
   }
   
   private boolean comment() {
      char start = original[read];
      
      if(comment(start)){
         if(read + 1 < count) {   
            char next = original[read + 1];
            
            if(next == '/') {
               while(read < count) {
                  char terminal = original[read++];
                  
                  if(terminal == '\n') {
                     clean[write++] = '\n';
                     return true;
                  }
                  clean[write++] = ' ';
               }
               return true; // end of source
            } 
            if(next == '*') {
               while(read < count) {
                  char terminal = original[read++];
                  
                  if(terminal == '/' && read > 1) {
                     char prev = original[read - 2];
                     
                     if(prev == '*') {
                        clean[write++] = ' ';
                        return true;
                     }
                  }
                  if(terminal == '\n') {
                     clean[write++] = '\n';
                  } else {
                     clean[write++] = ' ';
                  }
               }
               throw new IllegalStateException("Comment not closed");
            }
         }
      }
      return false;
   }
   
   private boolean string() {
      char start = original[read];
      
      if(quote(start)) {
         int size = 0;
         
         while(read < count) {
            char next = original[read];
            
            if(next == start) {
               if(size == 1) { // "" or ''
                  clean[write++] = original[read++];
                  return true; 
               } 
               if(read > 0 && size > 0) {
                  char prev = original[read - 1];
                  
                  if(!escape(prev)) {
                     clean[write++] = original[read++];
                     return true;
                  }
                  for(int i = 1; i <= size; i++) {
                     char value = original[read - i];
                     
                     if(!escape(value)) {
                        if(i % 2 == 1) {
                           clean[write++] = original[read++];
                           return true;
                        }
                        break;
                     }
                  }
               }
            }
            clean[write++] = original[read++];
            size++;
         }
         throw new IllegalStateException("String literal not closed");
      }
      return false;
   }
   
   private boolean escape(char value) {
      return value == '\\';
   }
   
   private boolean directive(char value) {
      return value == '#';
   }
   
   private boolean comment(char value) {
      return value == '/';
   }
   
   private boolean quote(char value) {
      switch(value){
      case '"': case '\'':
      case '`':
         return true;
      default:
         return false;
      }
   }
}
