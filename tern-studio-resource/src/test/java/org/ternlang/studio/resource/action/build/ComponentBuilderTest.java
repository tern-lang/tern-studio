package org.ternlang.studio.resource.action.build;

import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.QueryParam;
import org.ternlang.studio.resource.action.annotation.Required;
import org.ternlang.studio.resource.action.build.ComponentBuilder;
import org.ternlang.studio.resource.action.build.ConstructorScanner;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
import org.ternlang.studio.resource.action.extract.CookieExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.HeaderExtractor;
import org.ternlang.studio.resource.action.extract.ModelExtractor;
import org.ternlang.studio.resource.action.extract.PartExtractor;
import org.ternlang.studio.resource.action.extract.QueryExtractor;
import org.ternlang.studio.resource.action.extract.RequestExtractor;
import org.ternlang.studio.resource.action.extract.ResponseExtractor;

import junit.framework.TestCase;

public class ComponentBuilderTest extends TestCase {

   public static class SomeComponent {

      @Required
      @QueryParam("a")
      String a;

      @Required
      @QueryParam("b")
      String b;

      @Required
      @QueryParam("int")
      int value;

      @QueryParam("long")
      int num;
      String someFieldWithNoAnnotation = "X";

      public SomeComponent() {
         super();
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitConstructorParams extends SomeComponent {
      Request request;
      Model model;

      public SomeComponentWithImplicitConstructorParams(Model model, Request request) {
         this.model = model;
         this.request = request;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitAndExplicitConstructorParams extends SomeComponent {
      Request request;
      Model model;
      int someValue;

      public SomeComponentWithImplicitAndExplicitConstructorParams(@Required @Inject("int") int someValue, Model model, Request request) {
         this.model = model;
         this.request = request;
         this.someValue = someValue;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   public static class SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields extends SomeComponent {
      @Required
      @Inject
      Request request;
      Response response;
      @Required
      @Inject
      OutputStream output;
      Model model;
      int someValue;
      @QueryParam("a")
      String x;

      public SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields(@QueryParam("int") int someValue, Model model, Request request) {
         this.model = model;
         this.request = request;
         this.someValue = someValue;
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }
   
   @Path("/")
   private static class SomeResource {
      
      private final FooService foo;
      private final BlahService blah;
      
      public SomeResource(FooService foo, BlahService blah) {
         this.foo = foo;
         this.blah = blah;
      }
   }
   
   private static class FooService {
      
      private final String name;
      
      public FooService(String name) {
         this.name = name;
      }
   }

   private static class BlahService {
      
      private final String name;
      
      public BlahService(String name) {
         this.name = name;
      }
   }
   
   private ConstructorScanner scanner;

   public void setUp() {
      List<Extractor> extractors = new LinkedList<Extractor>();
      
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      
      Map<String, Object> dependency = new LinkedHashMap<String, Object>();
      DependencySystem system = new MapSystem(dependency);
      
      dependency.put("foo", new FooService("foo"));
      dependency.put("blah1", new BlahService("blah1"));
      dependency.put("blah2", new BlahService("blah2"));
      
      scanner = new ConstructorScanner(system, extractors);
   }
   
   public void testComponentBuilderWithConstructorInjection() throws Exception {
      List<ComponentBuilder> builders = scanner.createBuilders(SomeResource.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeResource component = (SomeResource) value;

      assertNotNull(value);
      assertNotNull(component.foo);
      assertEquals(component.foo.getClass(), FooService.class);
      assertNotNull(component.blah);
      assertEquals(component.blah.getClass(), BlahService.class);
   }
   

   public void testComponentBuilderWithImplicitParams() throws Exception {
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponentWithImplicitConstructorParams.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponentWithImplicitConstructorParams component = (SomeComponentWithImplicitConstructorParams) value;

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, "B");
      assertEquals(component.value, 5);
      assertEquals(component.someFieldWithNoAnnotation, "X");
      //assertEquals(component.request, request);
      assertEquals(component.model, context.getModel());
   }

   public void testComponentBuilderWithImplicitAndExplicitParams() throws Exception {
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields.class);
      MockRequest request = new MockRequest("GET", "/?a=A&b=B&int=5", "");
      MockResponse response = new MockResponse(System.err);
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields component = (SomeComponentWithImplicitAndExplicitConstructorParamsAndOverriddenFields) value;

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, "B");
      assertEquals(component.value, 5);
      assertEquals(component.someFieldWithNoAnnotation, "X");
      assertNull(component.response);
      assertEquals(component.output, System.err);
      assertEquals(component.request, request);
      assertEquals(component.model, context.getModel());
      assertEquals(component.someValue, 5);
      assertEquals(component.x, "A");
   }
}
