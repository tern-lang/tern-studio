package org.ternlang.studio.common.json;

import org.ternlang.parse.StringParser;
import org.ternlang.studio.common.json.document.DocumentAssembler;

public class JsonParser extends StringParser {   
   
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
   
   @Override
   protected boolean skip(String text){
      int size = text.length();
      int read = 0;

      if(off + size > count){
         return false;
      }
      while(read < size){
         char left = text.charAt(read);
         char right = source[off + read++];

         if(left != right){
            return false;
         }
      }
      off += size;
      return true;
   }
   
   private void pack() {
      int read = off;
      int write = 0;

      while(read < count){
         char next = source[read];
         
         if(next == '"'){ 
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
                  source[write++] = source[read++];
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
         if(skip("{")) {
            block();
         } else if(skip("[")) {
            array();               
         } else {
            throw new IllegalStateException("Could not parse JSON");
         }
      }
   }
   
   private void attribute() {
      name();
      
      if(!skip(":")) {
         throw new IllegalStateException("Attribute must be followed by :");
      }
      if(skip("{")) {
         block();
      }else if(skip("[")) {
         array();
      } else {
         value();
      }
      skip(",");
   }
   
   private void text() {
      int start = off;
            
      while(off < count) {
         char next = source[off++];
         
         if(next == '\"' && source[off -1] != '\'') {
            int length = (off - start) - 1;
            
            assembler.text(source, start, length);               
            return;
         }            
      }
      throw new IllegalStateException("Invalid text value");   
   }
   
   private void number(int sign) {
      double scale = 1.0d;
      long number = 0;
      int start = off;
      int spot = 0;
      
      while(off < count) {
         char next = source[off];
          
         if(next == ']' || next == '}' || next == ',') {
            int from = start + (sign == -1 ? 1 : 0);
            int length = off - start;
            
            if(spot > 0) {
               assembler.decimal(source, from, length, sign * number / scale); // lossy
            } else {
               assembler.integer(source, from, length, sign * number);
            }
            return;
         } else if(next == '.') {
            if(spot++ > 0) {
               throw new IllegalStateException("Invalid decimal value");               
            }            
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
   
   private void bool() {
      int start = off;
      
      if(skip("true")) {
         assembler.bool(source, start, off - start, true); 
      } else if(skip("false")) {
         assembler.bool(source, start, off - start, false); 
      } else {
         throw new IllegalStateException("Invalid boolean value");  
      }
   }
   
   private void none() {
      int start = off;
      
      while(off < count) {
         char next = source[off];
         
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            assembler.none(source, start, length); 
            return;
         }            
         off++;
      }
      throw new IllegalStateException("Invalid null value");  
   }
   
   private void value() {
      char value = source[off];
      
      if(value == '"') {
         off++;
         text();
      } else if(value == 't' || value == 'f') {
         bool();
      } else if(value == 'n') {
         none();
      } else if(value == '-') {
         off++;
         number(-1);         
      } else {
         if(value >= '0' && value <= '9') {
            number(1);
         } else {
            throw new IllegalStateException("Could not parse value");
         }
      }
   }
   
   private void name() {      
      int pos = off;
      
      while(pos < count) {
         char next = source[pos++];
         
         if(next == '\"') {
            int length = (pos -1) - off;
            int start = off;            
            
            off = pos;
            assembler.name(source, start, length);
            return;
         }
      }
      throw new IllegalStateException("Unexpected end of source");
   }
   
   private void block() {
      assembler.blockBegin();
      
      while(off < count) {
         if(skip("\"")) {
            attribute();
         } else if(skip("}")) {
            break;
         }
      }
      assembler.blockEnd();
   }
   
   private void array() {
      assembler.arrayBegin();
      
      while(off < count) {
         char next = source[off++];
         
         if(next == '[') {
            array();
         } else if(next == '{') {
            block();
         } else if(next == ']') {
            break;            
         } else {
            off--;
            value();   
         }
         skip(",");
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
