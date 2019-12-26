package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.PropertyConverter;

import junit.framework.TestCase;

public class ClassEntityTest extends TestCase {

   private static class Example {
      private String name;
      private int age;
      private long num;
      private boolean ready;
      private short sh;
      private double decimal;
      private String ignore;
      private Address address;
   }

   private static class Address {
      private String street;
      private String city;
   }
   
   public void testEntity() throws Exception {
      PropertyConverter converter = new PropertyConverter();
      ObjectBuilder builder = new ObjectBuilder();
      ClassProvider provider = new ClassProvider(builder, converter);
      Example example = new Example();
      
      example.name = "Niall";
      example.age = 10;
      example.num = 23646;
      example.ready = true;
      example.ignore = "ignore me";
      example.sh = (short)3546;
      example.decimal = -3534.235352353;
      example.address = new Address();
      example.address.street = "Thomas St";
      example.address.city = "London";
      
      Entity entity = provider.index(Example.class);
      
      assertTrue(entity.getProperty("name").getValue(example).toText() == "Niall");
      assertTrue(entity.getProperty("age").getValue(example).toInteger() == 10);
      assertTrue(entity.getProperty("num").getValue(example).toLong() == 23646);
      assertTrue(entity.getProperty("ready").getValue(example).toBoolean() == true);
      assertTrue(entity.getProperty("sh").getValue(example).toShort() == (short)3546);
      assertTrue(entity.getProperty("decimal").getValue(example).toDouble() == -3534.235352353);
      assertTrue(entity.getProperty("ignore").getValue(example).toText() == "ignore me");
      
      assertNull(entity.getProperty("street"));
      assertNull(entity.getProperty("city"));

      assertTrue(provider.getEntity("Address").getProperty("street").getValue(example.address).toText() == "Thomas St");
      assertTrue(provider.getEntity("Address").getProperty("city").getValue(example.address).toText() == "London");
   }
}
