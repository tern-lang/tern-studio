package org.ternlang.studio.service.json;

import junit.framework.TestCase;

public class JsonFormatterTest extends TestCase {

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
   
   public void testMapper() throws Exception {
      System.err.println(SOURCE_SMALL);
      JsonFormatter formatter = new JsonFormatter();
      System.err.println(formatter.format(SOURCE_SMALL));
      System.err.println(SOURCE_NORMAL);
      System.err.println(formatter.format(SOURCE_NORMAL));
   }
}
