package org.ternlang.studio.common.json;

import java.text.DecimalFormat;

import org.ternlang.studio.common.json.JsonParser;
import org.ternlang.studio.common.json.document.DirectAssembler;
import org.ternlang.studio.common.json.document.DocumentAssembler;
import org.ternlang.studio.common.json.document.DocumentHandler;
import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.PriorityAssembler;
import org.ternlang.studio.common.json.document.Value;
import org.ternlang.studio.common.json.operation.BlockType;

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
      DocumentAssembler assembler = new DirectAssembler(HANDLER);
      JsonParser parser = new JsonParser(assembler);
      parser.parse(SOURCE_SMALL);   
   }
   
   public void testParserPerf() throws Exception {
      parseSource("NORMAL SOURCE_SMALL", SOURCE_SMALL);
      parseSource("NORMAL SOURCE_NORMAL", SOURCE_NORMAL);
      parseSourceWithTypeAssembler("TYPE SOURCE_SMALL", SOURCE_SMALL, "name");
      parseSourceWithTypeAssembler("TYPE SOURCE_NORMAL", SOURCE_NORMAL, "BeginString");
   }
   
   private void parseSource(final String name, final String source) throws Exception {
      System.err.println(source);
      
      final double iterations = 1000000;
      final DecimalFormat format = new DecimalFormat("######.########");
      final DocumentHandler handler = new BlankHandler();   
      final DocumentAssembler assembler = new DirectAssembler(handler);
      final JsonParser parser = new JsonParser(assembler);
      final double gb = 1000000000;
      final double fraction = (source.length() * iterations) / gb;

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
      timeRun(name + " iterations (" + format.format(fraction) + " GB): " + iterations, task);
   }
   
   
   private void parseSourceWithTypeAssembler(final String name, final String source, final String type) throws Exception {
      System.err.println(source);

      final double iterations = 1000000;
      char[] text = type.toCharArray();
      final BlockType match = new BlockType(null).with(text, 0, text.length);
      final DecimalFormat format = new DecimalFormat("######.########");
      final DocumentHandler handler = new BlankHandler();
      final DocumentAssembler assembler = new PriorityAssembler(handler, match);
      final JsonParser parser = new JsonParser(assembler);
      final double gb = 1000000000;
      final double fraction = (source.length() * iterations) / gb;

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
      timeRun(name + " iterations (" + format.format(fraction) + " GB): " + iterations, task);
   }
   
   
   private static class BlankHandler implements DocumentHandler {

      @Override
      public void begin() {}

      @Override
      public void attribute(Name name, Value value) {}

      @Override
      public void blockBegin(Name name) {}

      @Override
      public void blockBegin(Name name, Name type) {}
      
      @Override
      public void blockEnd() {}

      @Override
      public void arrayBegin(Name name) {}

      @Override
      public void arrayEnd() {}

      @Override
      public void end() {}
      
   }
   
   private static final DocumentHandler HANDLER = new BlankHandler() {
      
      @Override
      public void attribute(Name name, Value value) {
         if (!name.isEmpty()) {
            System.err.println(name + "=" + value);
         } else {
            System.err.println(value);
         }
      }

      @Override
      public void blockBegin(Name name) {
         if (!name.isEmpty()) {
            System.err.print(name + ": ");
         }
         System.err.println("{");
      }

      @Override
      public void blockEnd() {
         System.err.println("}");
      }

      @Override
      public void arrayBegin(Name name) {
         if (!name.isEmpty()) {
            System.err.print(name + ": ");
         }
         System.err.println("[");
      }

      @Override
      public void arrayEnd() {
         System.err.println("]");
      }

   };
   
}
