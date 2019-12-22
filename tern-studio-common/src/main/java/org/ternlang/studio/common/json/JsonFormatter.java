package org.ternlang.studio.common.json;

import java.util.function.Consumer;

import org.ternlang.studio.common.json.document.DirectAssembler;
import org.ternlang.studio.common.json.document.DocumentAssembler;
import org.ternlang.studio.common.json.document.DocumentHandler;
import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.Value;

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
   private final FormatHandler handler;
   private final DocumentAssembler assembler;
   private final JsonParser parser;
   
   public JsonFormatter() {
      this(8192);
   }
   
   public JsonFormatter(int capacity) {
      this.builder = new StringBuilder(capacity);
      this.handler = new FormatHandler();
      this.assembler = new DirectAssembler(handler);
      this.parser = new JsonParser(assembler);
   }
   
   public void format(String json, Consumer<CharSequence> consumer) {
      parser.parse(json);
      consumer.accept(builder);
   }
   
   private static class FormatHandler implements DocumentHandler {
   
      private StringBuilder builder;
      private String value;
      private int indent;
      
      public FormatHandler() {
         this.builder = new StringBuilder(8192);
      }
      
      @Override
      public void begin() {
         indent = 0;
         builder.setLength(0);
      }

      @Override
      public void attribute(Name name, Value value) {
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
      public void blockBegin(Name name) {
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
      public void blockBegin(Name name, Name type) {
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
      public void blockEnd() {
         builder.append(INDENTS[--indent]);   
         builder.append("}\n");
      }
      
      @Override
      public void arrayBegin(Name name) {
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
      public void arrayEnd() {
         builder.append(INDENTS[--indent]);         
         builder.append("]\n");
      }
      
      @Override
      public void end() {
         value = builder.toString();
      }
      
      @Override
      public String toString() {
         return value;
      }
   }

}
