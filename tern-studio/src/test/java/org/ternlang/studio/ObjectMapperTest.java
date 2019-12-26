package org.ternlang.studio;

import java.text.DecimalFormat;

import org.ternlang.studio.common.json.object.ObjectMapper;
import org.ternlang.studio.common.json.object.ObjectReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.Gson;

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
      private PostCode postCode;
   }
   
   private static class FullAddress extends Address{
      private int house;
   }
   
   public static class PostCode {
      private String prefix;
      private String suffix;
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
   "      \"type\": \"FullAddress\",\n"+
   "      \"house\": 62,\n"+   
   "      \"street\": \"Flat 22,\\nWilliam St\",\n" +
   "      \"city\": \"Limerick\",\n" +
   "      \"postCode\": {\n" +
   "         \"prefix\": \"IVTTYYU\",\n" +   
   "         \"suffix\": \"EXCVITTX\"\n" +
   "       }\n" +
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

      reader.read(SOURCE);

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
                  assertEquals(example.address.postCode.prefix, "IVTTYYU");
                  assertEquals(example.address.postCode.suffix, "EXCVITTX");
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
            .register(FullAddress.class)
            .match("type");

      final ObjectReader reader = mapper.read(Object.class);
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * ITERATIONS) / gb;

      reader.read(SOURCE);

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
                  assertEquals(example.address.postCode.prefix, "IVTTYYU");
                  assertEquals(example.address.postCode.suffix, "EXCVITTX");
               }
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      };
      timeRun("INTERNAL TYPE iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }
   
   public void testMapperWithTypeAlias() throws Exception {
      final String source = SOURCE
            .replace("FullAddress", "FULL_ADDRESS")
            .replace("Address", "ADDRESS")
            .replace("Example", "EXAMPLE");
            
      System.err.println(source);

      final DecimalFormat format = new DecimalFormat("######.########");
      final ObjectMapper mapper = new ObjectMapper()
            .register(Example.class, "EXAMPLE")
            .register(Address.class, "ADDRESS")
            .register(FullAddress.class, "FULL_ADDRESS")
            .match("type");

      final ObjectReader reader = mapper.read(Object.class);
      final double gb = 1000000000;
      final double fraction = (source.length() * ITERATIONS) / gb;

      reader.read(source);

      final Runnable task = new Runnable() {

         public void run() {
            try {
               for(int i = 0; i < ITERATIONS; i++) {
                  Example example = reader.read(source);

                  assertEquals(example.name, "Niall Gallagher");
                  assertEquals(example.age, 101);
                  assertEquals(example.num, -13456734670093L);
                  assertEquals(example.ready, true);
                  assertEquals(((FullAddress)example.address).house, 62);
                  assertEquals(example.address.street, "Flat 22,\nWilliam St");
                  assertEquals(example.address.city, "Limerick");
                  assertEquals(example.address.postCode.prefix, "IVTTYYU");
                  assertEquals(example.address.postCode.suffix, "EXCVITTX");
               }
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
      };
      timeRun("INTERNAL ALIAS iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
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

      reader.readValue(SOURCE);

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
                  assertEquals(example.address.postCode.prefix, "IVTTYYU");
                  assertEquals(example.address.postCode.suffix, "EXCVITTX");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("JACKSON iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }

   public void testMapperGson() throws Exception {
      System.err.println(SOURCE);
      
      final DecimalFormat format = new DecimalFormat("######.########");
      final Gson gson = new Gson();
      final double gb = 1000000000;
      final double fraction = (SOURCE.length() * ITERATIONS) / gb;

      gson.fromJson(SOURCE, Example.class);

      final Runnable task = new Runnable() {
         
         public void run() {
            try {                
               for(int i = 0; i < ITERATIONS; i++) {
                  Example example = gson.fromJson(SOURCE, Example.class);
                  
                  assertEquals(example.name, "Niall Gallagher");
                  assertEquals(example.age, 101);
                  assertEquals(example.num, -13456734670093L);
                  assertEquals(example.ready, true);
                  assertEquals(example.address.street, "Flat 22,\nWilliam St");
                  assertEquals(example.address.city, "Limerick");
                  assertEquals(example.address.postCode.prefix, "IVTTYYU");
                  assertEquals(example.address.postCode.suffix, "EXCVITTX");
               }
            } catch(Exception e) {
               e.printStackTrace();
            } 
         }
      };
      timeRun("GSON iterations (" + format.format(fraction) + " GB): " + format.format(ITERATIONS), task);
   }
   
   public static void main(String[] list) throws Exception {
      ObjectMapperTest test = new ObjectMapperTest();
      
      test.testMapperWithTypeAlias();
      test.testMapperWithType();
      test.testMapper();
      test.testMapperJackson();
      test.testMapperGson();
   }
}
