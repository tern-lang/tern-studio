package org.ternlang.studio.service.json;

import org.ternlang.parse.StringParser;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.Name;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class JsonParser extends StringParser {   
   
   private final AttributeHandler handler;
   private final DecimalSlice decimal;
   private final IntegerSlice integer;
   private final BooleanSlice bool;
   private final NullSlice none;
   private final TextSlice text;
   private final NameSlice name;
   
   public JsonParser(AttributeHandler handler) {
      this.integer = new IntegerSlice();
      this.decimal = new DecimalSlice();
      this.bool = new BooleanSlice();
      this.none = new NullSlice();
      this.text = new TextSlice();
      this.name = new NameSlice();    
      this.handler = handler;      
   }
   
   @Override
   public void init() {      
      off = 0;
   }   

   @Override
   protected void parse() {
      handler.onBegin();
      pack();
      process();
      handler.onEnd();
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
   
   public void block() {
      handler.onBlockBegin(null);
      
      while(off < count) {
         if(skip("}")) {
            break;
         } else if(skip("\"")) {
            attribute();
         } else {
            throw new IllegalStateException("Could not parse JSON");
         }
      }
      handler.onBlockEnd(null);
   }
   
   public void array() {
      handler.onArrayBegin(null);
      
      while(off < count) {
         if(skip("}")) {
            break;
         }
         if(skip("\"")) {
            attribute();
         } else {
            throw new IllegalStateException("Could not parse JSON");
         }
      }
      handler.onArrayEnd(null);
   }
   
   private void attribute() {
      Name name = name();
      
      if(!skip(":")) {
         throw new IllegalStateException("Attribute must be followed by :");
      }
      if(skip("{")) {
         block(name);
      }else if(skip("[")) {
         array(name);
      } else {
         value(name);
      }
      skip(",");
   }
   
   private void text(Name name) {
      int start = off;
            
      while(off < count) {
         char next = source[off++];
         
         if(next == '\"' && source[off -1] != '\'') {
            int length = (off - start) - 1;
            
            text.with(source, start, length);               
            handler.onAttribute(name, text);
            return;
         }            
      }
   }
   
   private void number(Name name, int sign) {
      int start = off;
      int spot = 0;
      
      while(off < count) {
         char next = source[off];
          
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            if(spot > 0) {
               decimal.with(source, start + sign, length);
               handler.onAttribute(name, decimal);
            } else {
               integer.with(source, start + sign, length);
               handler.onAttribute(name, integer);
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
   
   private void bool(Name name) {
      int start = off;
      
      while(off < count) {
         char next = source[off];
         
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            bool.with(source, start, length); 
            handler.onAttribute(name, bool);
            return;
         }            
         off++;
      }
   }
   
   private void none(Name name) {
      int start = off;
      
      while(off < count) {
         char next = source[off];
         
         if(next == ']' || next == '}' || next == ',') {
            int length = off - start;
            
            none.with(source, start, length); 
            handler.onAttribute(name, none);
            return;
         }            
         off++;
      }
   }
   
   private void value(Name name) {
      char value = source[off];
      
      if(value == '"') {
         off++;
         text(name);
      } else if(value == 't' || value == 'f') {
         bool(name);
      } else if(value == 'n') {
         none(name);
      } else if(value == '-') {
         off++;
         number(name, -1);         
      } else {
         if(value >= '0' && value <= '9') {
            number(name, 0);
         } else {
            throw new IllegalStateException("Could not parse value for " + name);
         }
      }
   }
   
   private Name name() {      
      int pos = off;
      
      while(pos < count) {
         char next = source[pos++];
         
         if(next == '\"') {
            int length = (pos -1) - off;
            int start = off;            
            
            off = pos;
            return name.with(source, start, length);
         }
      }
      throw new IllegalStateException("Unexpected end of source");
   }
   
   private void block(Name name) {
      handler.onBlockBegin(name);
      
      while(off < count) {
         if(skip("\"")) {
            attribute();
         } else if(skip("}")) {
            break;
         }
      }
      handler.onBlockEnd(name);
   }
   
   private void array(Name name) {
      handler.onArrayBegin(name);
      
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
            value(null);   
         }
         skip(",");
      }
      handler.onArrayEnd(name);
   }
   
   private static class NameSlice implements Name {
      
      private final Slice slice = new Slice();    
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public NameSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }

   }
   
   private static class TextSlice implements TextValue {
      
      private final Slice slice = new Slice();    
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public TextSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class NullSlice implements NullValue {
      
      private final Slice slice = new Slice();    
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public NullSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class BooleanSlice implements BooleanValue {
      
      private final Slice slice = new Slice(); 
      private boolean value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }

      public BooleanSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public boolean toBoolean() {
         return value;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class IntegerSlice implements IntegerValue {
      
      private final Slice slice = new Slice(); 
      private long value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }

      public IntegerSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public long toLong() {
         return value;
      }

      @Override
      public int toInteger() {
         return (int)value;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
   
   private static class DecimalSlice implements DecimalValue {
      
      private final Slice slice = new Slice(); 
      private double value;
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
     
      public DecimalSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
      }

      @Override
      public double toDouble() {
         return value;
      }

      @Override
      public float toFloat() {
         return (float)value;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
}
