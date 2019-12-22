package org.ternlang.studio.common.json;

import org.ternlang.parse.StringParser;
import org.ternlang.studio.common.json.document.DocumentAssembler;

public class JsonParser extends StringParser { 
   
   private static final char[] TRUE = { 't', 'r', 'u', 'e' };
   private static final char[] FALSE = { 'f', 'a', 'l', 's', 'e' };
   private static final char[] NULL = { 'n', 'u', 'l', 'l' };
   
   private final DocumentAssembler assembler;
   
   public JsonParser(DocumentAssembler assembler) {
      this.assembler = assembler;     
   }
   
   @Override
   public void init() {      
      off = 0;
   }   

   @Override
   protected void parse() {
      assembler.begin();
      pack();
      process();
      assembler.end();
   }
   
   private void pack() {
      int read = off;
      int write = 0;

      while(read < count){
         char next = source[read];
         
         if(next == '"'){ 
            int insert = write + 1;
            
            source[write++] = source[read++];
            write++;
            
            while(read < count) {
               source[write++] = source[read++];
               
               if(source[read - 1] == '\\') {
                  if(read >= count) {
                     throw new IllegalStateException("String not closed");
                  }
                  char special = source[read++];
                  char replace = escape(special);
                  
                  source[write - 1] = replace;
               } else if(source[read] == '"') {
                  int length = (write - insert) - 1;
                  
                  source[insert] = (char)length;
                  read++;
                  break;
               }
            }
         } else if(!space(next)) {
            source[write++] = source[read++];
         } else {
            read++;
         }
      }
      count = write;
   }
   
   private void process() {
      while(off < count) {
         if(source[off] == '{') {
            block();
         } else if(source[off] == '[') {
            array();               
         } else {
            throw new IllegalStateException("Could not parse JSON");
         }
      }
   }
   
   private void attribute() {
      name();
      
      if(off >= count) {
         throw new IllegalStateException("Unexpected end of source");
      }
      value();
      
      if(off < count && source[off] == ',') {
         off++;
      }
   }
   
   private void name() {   
      char open = source[off++];
      
      if(open != '"') {
         throw new IllegalStateException("Name must start with a quote");
      }
      int length = source[off++];
      
      assembler.name(source, off, length);
      off += length;
   }
   
   private void value() {
      char open = source[off++];
      
      if(open != ':') {
         throw new IllegalStateException("Attribute value not prefixed by :");
      }
      if(source[off] == '{') {
         block();
      }else if(source[off] == '[') {
         array();
      } else {
         token();
      }
   }
   
   private void text() {
      char open = source[off++];
      
      if(open != '"') {
         throw new IllegalStateException("Text must start with a quote");
      }
      int length = source[off++];
      
      assembler.text(source, off, length);
      off += length;
   }
   
   private void number(int sign) {
      double scale = 1.0d;
      long number = 0;
      int start = off;
      int spot = 0;
      
      while(off < count) {
         char next = source[off];
          
         if(next == '.') {
            if(spot++ > 0) {
               throw new IllegalStateException("Invalid decimal value");               
            }   
         } else if(next < '0' || next > '9') {
            int from = start + (sign == -1 ? 1 : 0);
            int length = off - start;
            
            if(spot > 0) {
               assembler.decimal(source, from, length, sign * number / scale); // lossy
            } else {
               assembler.integer(source, from, length, sign * number);
            }
            return;
         }  
         if(spot > 0) {
            scale *= 10;
         }
         number *= 10;
         number += next;
         number -= '0';
         off++;
      }
   }
   
   private void bool(boolean expect) {
      int start = off;
      int index = 0;
      
      if(expect) {
         while(off < count && index < TRUE.length) {
            if(TRUE[index++] != source[off++]) {
               throw new IllegalStateException("Invalid boolean value");
            }
         }
         assembler.bool(source, start, TRUE.length, true); 
      } else {
         while(off < count && index < FALSE.length) {
            if(FALSE[index++] != source[off++]) {
               throw new IllegalStateException("Invalid boolean value");
            }
         }
         assembler.bool(source, start, FALSE.length, false); 
      }
   }
   
   private void none() {
      int start = off;
      int index = 0;
      
      while(off < count && index < NULL.length) {
         if(NULL[index++] != source[off++]) {
            throw new IllegalStateException("Invalid boolean value");
         }
      }
      assembler.bool(source, start, NULL.length, true); 
   }
   
   private void token() {
      char value = source[off];
      
      switch(value) {
      case '"':
         text();
         break;
      case 'f': 
         bool(false);
         break;
      case 't':
         bool(true);
         break;
      case 'n':   
         none();
         break;
      case '-':   
         off++;
         number(-1);  
         break;
      case '0': case '1':
      case '2': case '3':
      case '4': case '5':
      case '6': case '7':
      case '8': case '9':         
         number(1);   
         break;
      default:
         throw new IllegalStateException("Could not parse value");
      }
   }
   
   private void block() {
      char open = source[off++];
      
      if(open != '{') {
         throw new IllegalStateException("Block must begin with {");
      }
      assembler.blockBegin();
      
      while(off < count) {
         char next = source[off++];
         
         if(next == '"') {
            off--;
            attribute();
         } else if(next == '}') {
            break;
         }
      }
      assembler.blockEnd();
   }
   
   private void array() {
      char open = source[off++];
      
      if(open != '[') {
         throw new IllegalStateException("Array must begin with [");
      }
      assembler.arrayBegin();

      while(off < count) {
         char next = source[off];
         
         if(next == ']') {
            off++;
            break;
         }
         switch(next) {
         case ',':
            off++;
            break;
         case '[':
            array();
            break;
         case '{':
            block();
            break;          
         default:
            token();   
         }

      }
      assembler.arrayEnd();
   }
   
   private char escape(char value) {
      switch(value) {
      case 'b':
         return '\b';
      case 'f':
         return '\f';
      case 'n':
         return '\n';
      case 'r':
         return '\r';
      case 't':
         return '\t';
      }
      return value;
   }
}
