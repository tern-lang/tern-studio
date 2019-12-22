package org.ternlang.studio.service.json.test;

import java.text.DecimalFormat;

import org.ternlang.studio.service.json.object.ObjectMapper;
import org.ternlang.studio.service.json.object.ObjectReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class ObjectMapperTest extends PerfTestCase {
   
   private static class Example {
      private String name;
      //private int age;
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
   //"   \"age\": 101,\n" +
   "   \"attrs\": [\n" +
   "      \"one\",\n" +
   "      \"2\",\n" +
   "      \"3\"\n" +
   "   ],\n" +
   "   \"address\": {\n" +
   "      \"street\": \"William St\",\n" +
   "      \"city\": \"Limerick\"\n" +
   "   },\n" +
   "   \"type\": \"Example\"\n"+
   "}\n";         
   
   public void testMapper() throws Exception {
      System.err.println(SOURCE);
      
      final double iterations = 1000000;
      final DecimalFormat format = new DecimalFormat("######.########");
      final ObjectMapper mapper = new ObjectMapper();
      final ObjectReader reader = mapper.resolve(Example.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * iterations) / gb;

      final Runnable task = new Runnable() {
         
         public void run() {
            try {                
               for(int i = 0; i < iterations; i++) {
                  Example example = reader.read(SOURCE);
                  
                  assertEquals(example.name, "Niall Gallagher");
                  //assertEquals(example.age, 101);
                  assertEquals(example.address.street, "William St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("INTERNAL NORMAL iterations (" + format.format(fraction) + " GB): " + iterations , task);
   }

   public void testMapperWithType() throws Exception {
      System.err.println(SOURCE);

      final double iterations = 1000000;
      final DecimalFormat format = new DecimalFormat("######.########");
      final ObjectMapper mapper = new ObjectMapper()
            .register(Example.class)
            .register(Address.class)
            .match("type");

      final ObjectReader reader = mapper.resolve(Object.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * iterations) / gb;

      final Runnable task = new Runnable() {

         public void run() {
            try {
               for(int i = 0; i < iterations; i++) {
                  Example example = reader.read(SOURCE);

                  assertEquals(example.name, "Niall Gallagher");
                  //assertEquals(example.age, 101);
                  assertEquals(example.address.street, "William St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      };
      timeRun("INTERNAL TYPE iterations (" + format.format(fraction) + " GB): " + iterations , task);
   }
   
   public void testMapperJackson() throws Exception {
      System.err.println(SOURCE);
      
      final double iterations = 1000000;
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
      final double fraction = (SOURCE.length() * iterations) / gb;

      final Runnable task = new Runnable() {
         
         public void run() {
            try {                
               for(int i = 0; i < iterations; i++) {
                  Example example = reader.readValue(SOURCE);
                  
                  assertEquals(example.name, "Niall Gallagher");
                  //assertEquals(example.age, 101);
                  assertEquals(example.address.street, "William St");
                  assertEquals(example.address.city, "Limerick");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("JACKSON iterations (" + format.format(fraction) + " GB): " + iterations , task);
   }

}
