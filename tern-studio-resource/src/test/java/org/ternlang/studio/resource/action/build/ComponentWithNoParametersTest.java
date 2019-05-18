package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.annotation.Inject;
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

public class ComponentWithNoParametersTest extends TestCase {

   @Component
   public static class SomeComponent {

      String a;
      String b;

      @Required
      @QueryParam("c")
      String c;

      public SomeComponent(@Required @QueryParam("a") String a, @Required @QueryParam("b") String b) {
         this.a = a;
         this.b = b;
      }
   }

   public static class SomeComponentWithSomeComponent extends SomeComponent {

      @Required
      @Inject
      SomeComponent component;

      @Required
      @QueryParam("c")
      String c;

      public SomeComponentWithSomeComponent(@Required @QueryParam("a") String a) {
         super(a, null);
      }
   }

   public void testBuilder() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponent.class);
      MockRequest request = new MockRequest("GET", "/?a=A", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponent component = (SomeComponent) value;

      System.err.println(context.getValidation().getErrors());

      assertNotNull(value);
      assertEquals(component.a, "A");
      assertEquals(component.b, null);
      assertEquals(component.c, null);
      assertFalse(context.getValidation().isValid());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }

   public void testBuilderWithNoParametersAtAll() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      List<ComponentBuilder> builders = scanner.createBuilders(SomeComponentWithSomeComponent.class);
      MockRequest request = new MockRequest("GET", "/", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      ComponentBuilder builder = builders.iterator().next();
      Object value = builder.build(context);
      SomeComponentWithSomeComponent component = (SomeComponentWithSomeComponent) value;

      System.err.println(context.getValidation().getErrors());

      assertNotNull(component.component);
      assertEquals(component.component.a, null);
      assertEquals(component.component.b, null);
      assertEquals(component.component.c, null);
      assertNotNull(value);
      assertEquals(component.a, null);
      assertEquals(component.b, null);
      assertEquals(component.c, null);
      assertFalse(context.getValidation().isValid());

   }
}
