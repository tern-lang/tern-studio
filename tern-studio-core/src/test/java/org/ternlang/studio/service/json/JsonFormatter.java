package org.ternlang.studio.service.json;

import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.Name;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class JsonFormatter {
   
   private static final String[] INDENTS = new String[255];
   
   static {
      StringBuilder builder = new StringBuilder(INDENTS.length);
      
      for(int i = 0; i < INDENTS.length; i++) {
         INDENTS[i] = builder.toString();
         
         for(int j = 0; j < 3; j++) {
            builder.append(" ");
         }
      }
   }
   
   public String format(String json) {
      JsonHandler handler = new JsonHandler();
      JsonParser parser = new JsonParser(handler);
      
      parser.parse(json);
      return handler.toString();
   }
   
   private static class JsonHandler implements AttributeHandler {
   
      private StringBuilder builder;
      private String value;
      private int indent;
      
      public JsonHandler() {
         this.builder = new StringBuilder(8192);
      }
      
      @Override
      public void onBegin() {
         indent = 0;
         builder.setLength(0);
      }
      
      @Override
      public void onAttribute(Name name, TextValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, IntegerValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, DecimalValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, BooleanValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
      
      @Override
      public void onAttribute(Name name, NullValue value) {
         CharSequence token = value.toToken();
         onAttribute(name, token);
      }
 
      private void onAttribute(Name name, CharSequence value) {
         builder.append(INDENTS[indent]);
         
         if(name != null) {    
            builder.append('"');
            builder.append(name);
            builder.append("\": \"");
            builder.append(value);
            builder.append('"');
         } else {
            builder.append('"');
            builder.append(value);
            builder.append('"');
         }
         builder.append(',');
         builder.append('\n');
      }
      
      @Override
      public void onBlockBegin(Name name) {
         builder.append(INDENTS[indent++]);
         
         if(name != null) {      
            builder.append('"');
            builder.append(name);
            builder.append("\": {\n");
         } else {
            builder.append("{\n");
         }
      }
      
      @Override
      public void onBlockEnd(Name name) {
         builder.append(INDENTS[--indent]);   
         builder.append("}\n");
      }
      
      @Override
      public void onArrayBegin(Name name) {
         builder.append(INDENTS[indent++]);
         
         if(name != null) {      
            builder.append('"');
            builder.append(name);
            builder.append("\": [\n");
         } else {
            builder.append("[\n");
         }
      }
      
      @Override
      public void onArrayEnd(Name name) {        
         builder.append(INDENTS[--indent]);         
         builder.append("]\n");
      }
      
      @Override
      public void onEnd() { 
         value = builder.toString();
      }
      
      @Override
      public String toString() {
         return value;
      }

   }

}
