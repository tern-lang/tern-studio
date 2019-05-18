package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.QueryParam;
import org.ternlang.studio.resource.action.annotation.Required;
import org.ternlang.studio.resource.action.build.ComponentFinder;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
import org.ternlang.studio.resource.action.build.MethodDispatcher;
import org.ternlang.studio.resource.action.build.MethodScanner;
import org.ternlang.studio.resource.action.build.MethodScannerResolver;
import org.ternlang.studio.resource.action.extract.CookieExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.HeaderExtractor;
import org.ternlang.studio.resource.action.extract.ModelExtractor;
import org.ternlang.studio.resource.action.extract.PartExtractor;
import org.ternlang.studio.resource.action.extract.QueryExtractor;
import org.ternlang.studio.resource.action.extract.RequestExtractor;
import org.ternlang.studio.resource.action.extract.ResponseExtractor;

import junit.framework.TestCase;

public class MethodValidationTest extends TestCase {

   @Component
   public static class SomeComponentWithNoInstantiation {
      private String x;

      public SomeComponentWithNoInstantiation(String x) {
         this.x = x;
      }
   }

   @Component
   public static class SomeComponent {
      private String y;

      public SomeComponent(@Required @QueryParam("z") String y) {
         this.y = y;
      }
   }

   @Path("/test")
   public static class SomeExampleController {

      private SomeComponentWithNoInstantiation shouldBeNull;
      private SomeComponent component;
      private String x;
      private Integer d;

      @Path
      public void execute(
            Model model, 
            SomeComponent component, 
            SomeComponentWithNoInstantiation shouldBeNull, 
            @Required @QueryParam("a") String x,
            @Required @QueryParam("y") Integer d) 
      {
         this.component = component;
         this.x = x;
         this.d = d;
         model.setAttribute("this", this);
      }
   }

   public void testCompositeController() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ComponentFinder finder = new ComponentFinder(SomeExampleController.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/test/execute?a=XX&z=DD", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertTrue(context.getModel().isEmpty());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }

   public void testWithNothingAtAll() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ComponentFinder finder = new ComponentFinder(SomeExampleController.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/test/execute", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertTrue(context.getModel().isEmpty());
      assertEquals(context.getValidation().getErrors().size(), 2);

   }
}
