package org.ternlang.studio.service.json;

import org.ternlang.parse.StringParser;
import org.ternlang.studio.service.json.document.DocumentAssembler;

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
      int start = off;
      int spot = 0;
      
      while(off < count) {
         char next = source[off];
          
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            if(spot > 0) {
               assembler.decimal(source, start + sign, length);
            } else {
               assembler.integer(source, start + sign, length);
            }
            return;
         } else if(next == '.') {
            if(spot++ > 0) {
               throw new IllegalStateException("Invalid decimal value");               
            }            
         }
         off++;
      }
   }
   
   private void bool() {
      int start = off;
      
      while(off < count) {
         char next = source[off];
         
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            assembler.bool(source, start, length); 
            return;
         }            
         off++;
      }
      throw new IllegalStateException("Invalid boolean value");  
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
            number(0);
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
}
