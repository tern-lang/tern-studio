package org.ternlang.studio.resource.action.build;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.extract.Extractor;

import junit.framework.TestCase;

public class ListOfComponentsTest extends TestCase {
   
   @Component
   public static class SomeComponent implements Serializable {
      
   }
   
   @Component
   public static class OtherComponent implements Serializable {
      
      private final SomeComponent a;
      private final YetAnotherComponent b;
      
      public OtherComponent(SomeComponent a, YetAnotherComponent b) {
         this.a = a;
         this.b = b;
      }
   }
   
   @Component
   public static class YetAnotherComponent implements Serializable {
      
      private SomeComponent a;
   
      public YetAnotherComponent(SomeComponent a) {
         this.a = a;
      }
   }
   
   @Component
   public static class SomeOtherComponent {
      
      private final SomeComponent a;
      private final OtherComponent b;
      
      public SomeOtherComponent(SomeComponent a, OtherComponent b) {
         this.a = a;
         this.b = b;
      }
   }
   
   @Component
   public static class ListOfSerializable {
      
      private final List<Serializable> list;
      
      public ListOfSerializable(List<Serializable> list) {
         this.list = list;
      }
   }

   public void testDepdencyInjection() throws Exception{
      List<Extractor> extractors = new LinkedList<Extractor>();
      DependencySystem dependencySystem = new MapSystem();
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeOtherComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      SomeOtherComponent value = builder.build(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
      
      List<Serializable> serializables = dependencySystem.resolveAll(Serializable.class);
      
      assertNotNull(serializables);
      assertFalse(serializables.isEmpty());
   }
   
   public void testDepdencyInjectionListOfComponents() throws Exception{
      List<Extractor> extractors = new LinkedList<Extractor>();
      DependencySystem dependencySystem = new MapSystem();
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeOtherComponent.class);
      List<ComponentBuilder> listBuilders = scanner.createBuilders(ListOfSerializable.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      SomeOtherComponent value = builder.build(context);
      
      assertNotNull(value);
      assertNotNull(value.a);
      assertNotNull(value.b);
      assertNotNull(value.b.a);
      assertNotNull(value.b.b);
      assertNotNull(value.b.b.a);
      
      ComponentBuilder listBuilder = listBuilders.iterator().next();
      ListOfSerializable listOfSerializable = listBuilder.build(context);
      
      assertNotNull(listOfSerializable);
   }
}