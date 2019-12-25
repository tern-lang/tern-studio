package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.entity.EntityWriter;
import org.ternlang.studio.common.json.entity.PropertyConverter;

import junit.framework.TestCase;

public class EntityWriterTest extends TestCase {
   
   private static class Example {
      private String name;
      private int age;
      private long num;
      private boolean ready;
      private String ignore;
      private Address address;
   }

   private static class Address {
      private String street;
      private String city;
   }
   

   public void testWriter() throws Exception {
      PropertyConverter converter = new PropertyConverter();
      ObjectBuilder builder = new ObjectBuilder();
      ClassProvider provider = new ClassProvider(builder, converter);
      EntityWriter writer = new EntityWriter(provider, converter);
      Example example = new Example();
      
      example.name = "Niall";
      example.age = 10;
      example.num = 23646;
      example.ready = true;
      example.ignore = "ignore me";
      example.address = new Address();
      example.address.street = "Thomas St";
      example.address.city = "London";
      
      provider.index(Example.class);
      
      CharSequence sequence = writer.write(example);
      
      System.out.println(sequence);
   }
}
