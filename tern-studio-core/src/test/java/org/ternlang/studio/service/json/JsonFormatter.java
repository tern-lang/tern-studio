package org.ternlang.studio.service.json;

import java.util.function.Consumer;

import org.ternlang.studio.service.json.document.DirectAssembler;
import org.ternlang.studio.service.json.document.DocumentAssembler;
import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.Value;

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
   
   private final StringBuilder builder;
   private final JsonHandler handler;
   private final DocumentAssembler assembler;
   private final JsonParser parser;
   
   public JsonFormatter() {
      this(8192);
   }
   
   public JsonFormatter(int capacity) {
      this.builder = new StringBuilder(capacity);
      this.handler = new JsonHandler();
      this.assembler = new DirectAssembler(handler);
      this.parser = new JsonParser(assembler);
   }
   
   public void format(String json, Consumer<CharSequence> consumer) {
      parser.parse(json);
      consumer.accept(builder);
   }
   
   private static class JsonHandler implements DocumentHandler {
   
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
      public void onAttribute(Name name, Value value) {
         builder.append(INDENTS[indent]);
         
         if(!name.isEmpty()) {    
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
         
         if(!name.isEmpty()) {      
            builder.append('"');
            builder.append(name);
            builder.append("\": {\n");
         } else {
            builder.append("{\n");
         }
      }
      
      @Override
      public void onBlockBegin(Name name, Name type) {
         builder.append(INDENTS[indent++]);
         
         if(!name.isEmpty()) {      
            builder.append('"');
            builder.append(name);
            builder.append("\": {\n");
         } else {
            builder.append("{\n");
         }
      }
      
      @Override
      public void onBlockEnd() {
         builder.append(INDENTS[--indent]);   
         builder.append("}\n");
      }
      
      @Override
      public void onArrayBegin(Name name) {
         builder.append(INDENTS[indent++]);
         
         if(!name.isEmpty()) {      
            builder.append('"');
            builder.append(name);
            builder.append("\": [\n");
         } else {
            builder.append("[\n");
         }
      }
      
      @Override
      public void onArrayEnd() {        
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
