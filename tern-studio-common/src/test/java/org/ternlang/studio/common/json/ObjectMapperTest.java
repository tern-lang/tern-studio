package org.ternlang.studio.common.json;

import java.text.DecimalFormat;

import org.ternlang.studio.common.json.entity.EntityReader;
import org.ternlang.studio.common.json.object.ObjectMapper;
import org.ternlang.studio.common.json.object.ObjectReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class ObjectMapperTest extends PerfTestCase {
   
   private static final double ITERATIONS = 1000000;
   
   private static class Example {
      private String name;
      private int age;
      private long num;
      private boolean ready;
      private String ignore;
      private String[] attrs;
      private Address address;
   }

   private static class Address {
      private String street;
      private String city;
   }

   private static final String SOURCE = 
   "{\n" +
   "   \"name\": \"Niall Gallagher\",\n" +
   "   \"age\": 101,\n" +
   "   \"num\": -13456734670093,\n" +   
   "   \"attrs\": [\n" +
   "      \"one\",\n" +
   "      \"2\",\n" +
   "      \"3\"\n" +
   "   ],\n" +
   "   \"address\": {\n" +
   "      \"street\": \"Flat 22,\\nWilliam St\",\n" +
   "      \"city\": \"Limerick\"\n" +
   "   },\n" +
   "   \"type\": \"Example\",\n"+
   "   \"ready\": true\n"+
   "}\n";         
   
   public void testMapper() throws Exception {
      System.err.println(SOURCE);
      

      final DecimalFormat format = new DecimalFormat("######.########");
      final ObjectMapper mapper = new ObjectMapper();
      final ObjectReader reader = mapper.read(Example.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * ITERATIONS) / gb;

      final Runnable task = new Runnable() {
         
         public void run() {
            try {                
               for(int i = 0; i < ITERATIONS; i++) {
                  Example example = reader.read(SOURCE);
                  
                  assertEquals(example.name, "Niall Gallagher");
                  assertEquals(example.age, 101);
                  assertEquals(example.num, -13456734670093L);
                  assertEquals(example.ready, true);
                  assertEquals(example.address.street, "Flat 22,\nWilliam St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("INTERNAL NORMAL iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }

   public void testMapperWithType() throws Exception {
      System.err.println(SOURCE);

      final DecimalFormat format = new DecimalFormat("######.########");
      final ObjectMapper mapper = new ObjectMapper()
            .register(Example.class)
            .register(Address.class)
            .match("type");

      final ObjectReader reader = mapper.read(Object.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * ITERATIONS) / gb;

      final Runnable task = new Runnable() {

         public void run() {
            try {
               for(int i = 0; i < ITERATIONS; i++) {
                  Example example = reader.read(SOURCE);

                  assertEquals(example.name, "Niall Gallagher");
                  assertEquals(example.age, 101);
                  assertEquals(example.num, -13456734670093L);
                  assertEquals(example.ready, true);
                  assertEquals(example.address.street, "Flat 22,\nWilliam St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      };
      timeRun("INTERNAL TYPE iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }
   
   public void testMapperJackson() throws Exception {
      System.err.println(SOURCE);
      
      final DecimalFormat format = new DecimalFormat("######.########");
      final com.fasterxml.jackson.databind.ObjectMapper mapper = 
            new com.fasterxml.jackson.databind.ObjectMapper();
      
      mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      
      final com.fasterxml.jackson.databind.ObjectReader reader = 
            mapper.readerFor(Example.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * ITERATIONS) / gb;

      final Runnable task = new Runnable() {
         
         public void run() {
            try {                
               for(int i = 0; i < ITERATIONS; i++) {
                  Example example = reader.readValue(SOURCE);
                  
                  assertEquals(example.name, "Niall Gallagher");
                  assertEquals(example.age, 101);
                  assertEquals(example.num, -13456734670093L);
                  assertEquals(example.ready, true);
                  assertEquals(example.address.street, "Flat 22,\nWilliam St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("JACKSON iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }

}
