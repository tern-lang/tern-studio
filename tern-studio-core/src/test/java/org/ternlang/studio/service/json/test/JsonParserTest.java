package org.ternlang.studio.service.json.test;

import org.ternlang.studio.service.json.DirectAssembler;
import org.ternlang.studio.service.json.JsonAssembler;
import org.ternlang.studio.service.json.JsonParser;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.BooleanValue;
import org.ternlang.studio.service.json.handler.DecimalValue;
import org.ternlang.studio.service.json.handler.IntegerValue;
import org.ternlang.studio.service.json.handler.Name;
import org.ternlang.studio.service.json.handler.NullValue;
import org.ternlang.studio.service.json.handler.TextValue;

public class JsonParserTest extends PerfTestCase {

   private static final String SOURCE_SMALL = 
   "{\n" +
   "   \"name\": \"Niall Gallagher\",\n" +
   "   \"age\": 101,\n" +
   "   \"attrs\": [\n" +
   "      \"one\",\n" +
   "      2,\n" +
   "      3\n" +
   "   ],\n" +
   "   \"address\": {\n" +
   "      \"street\": \"William St\",\n" +
   "      \"city\": \"Limerick\"\n" +
   "   }\n" +
   "}\n";      
   
   private static final String SOURCE_NORMAL =
   "{\n" +
   "    \"Header\": {\n" +
   "        \"BeginString\": \"FIXT.1.1\",\n" +
   "        \"MsgType\": \"W\",\n" +
   "        \"MsgSeqNum\": \"4567\",\n" +
   "        \"SenderCompID\": \"SENDER\",\n" +
   "        \"TargetCompID\": \"TARGET\",\n" +
   "        \"SendingTime\": \"20160802-21:14:38.717\"\n" +
   "    },\n" +
   "    \"Body\": {\n" +
   "        \"SecurityIDSource\": \"8\",\n" +
   "        \"SecurityID\": \"ESU6\",\n" +
   "        \"MDReqID\": \"789\",\n" +
   "        \"NoMDEntries\": [\n" +
   "            { \"MDEntryType\": \"0\", \"MDEntryPx\": \"1.50\", \"MDEntrySize\": \"75\", \"MDEntryTime\": \"21:14:38.688\" },\n" +
   "            { \"MDEntryType\": \"1\", \"MDEntryPx\": \"1.75\", \"MDEntrySize\": \"25\", \"MDEntryTime\": \"21:14:38.688\" }\n" +
   "        ]\n" +
   "    },\n" +
   "    \"Trailer\": {\n" +
   "    }\n" +
   "}\n";         
   
   public void testParser() throws Exception {
      JsonAssembler assembler = new DirectAssembler(HANDLER);
      JsonParser parser = new JsonParser(assembler);
      parser.parse(SOURCE_SMALL);   
   }
   
   public void testParserPerf() throws Exception {
      parseSource("SOURCE_SMALL", SOURCE_SMALL);
      parseSource("SOURCE_NORMAL", SOURCE_NORMAL);
   }
   
   private void parseSource(String name, String source) throws Exception {
      System.err.println(source);
      
      final int iterations = 1000000;
      final AttributeHandler handler = new BlankHandler();   
      final JsonAssembler assembler = new DirectAssembler(handler);
      final JsonParser parser = new JsonParser(assembler);
      
      parser.parse(source);
      parser.parse(source);
      
      final Runnable task = new Runnable() {
         
         public void run() {
            try {    
               for(int i = 0; i < iterations; i++) {
                  parser.parse(source);
               }
            } catch(Exception e) {
               e.printStackTrace();               
            }
         }
      };
      timeRun(name + " iterations: " + iterations, task);
   }
   
   
   private static class BlankHandler implements AttributeHandler {

      @Override
      public void onBegin() {}

      @Override
      public void onAttribute(Name name, TextValue value) {}

      @Override
      public void onAttribute(Name name, IntegerValue value) {}

      @Override
      public void onAttribute(Name name, DecimalValue value) {}

      @Override
      public void onAttribute(Name name, BooleanValue value) {}

      @Override
      public void onAttribute(Name name, NullValue value) {}

      @Override
      public void onBlockBegin(Name name) {}

      @Override
      public void onBlockBegin(Name name, Name type) {}
      
      @Override
      public void onBlockEnd() {}

      @Override
      public void onArrayBegin(Name name) {}

      @Override
      public void onArrayEnd() {}

      @Override
      public void onEnd() {}
      
   }
   
   private static final AttributeHandler HANDLER = new BlankHandler() {
      
      @Override
      public void onAttribute(Name name, TextValue value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }
      
      @Override
      public void onAttribute(Name name, IntegerValue value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }
      
      @Override
      public void onAttribute(Name name, DecimalValue value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }
      
      @Override
      public void onAttribute(Name name, BooleanValue value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }
      
      @Override
      public void onAttribute(Name name, NullValue value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }

      @Override
      public void onBlockBegin(Name name) {
         if (!name.isEmpty()) {
            System.err.print(name + ": ");
         }
         System.err.println("{");
      }

      @Override
      public void onBlockEnd() {
         System.err.println("}");
      }

      @Override
      public void onArrayBegin(Name name) {
         if (!name.isEmpty()) {
            System.err.print(name + ": ");
         }
         System.err.println("[");
      }

      @Override
      public void onArrayEnd() {
         System.err.println("]");
      }

   };
   
}
