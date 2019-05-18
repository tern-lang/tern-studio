package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Action;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.annotation.Intercept;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.build.ActionBuilder;
import org.ternlang.studio.resource.action.build.ComponentFinder;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
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

public class ActionBuilderTest extends TestCase {

   @Intercept("/a/b/c")
   public static class InterceptorA {
      @Intercept(".*")
      public void addString(Model model) {
         model.setAttribute("a", "A");
      }
   }

   @Intercept("/a")
   public static class InterceptorB {
      @Intercept(".*")
      public void addString(Model model) {
         model.setAttribute("b", "B");
      }
   }

   @Path("/a/b/c")
   public static class ServiceA {
      
      @GET
      @Path
      public void showA(Model model) {
         model.setAttribute("method", "showA");
      }

      @GET
      @Path
      public void showB(Model model) {
         model.setAttribute("method", "showB");
      }
   }

   @Path("/a/bad/service")
   public static class ServiceB {
      
      @GET
      @Path
      public void throwException(Model model) {
         throw new IllegalStateException("Catch me!!");
      }
   }

   @Path("/interpolate/exception")
   public static class ServiceC {
      
      @GET
      @Path
      public void throwException(Model model) {
         throw new IllegalStateException("Catch me!!");
      }
   }

   public void testActionBuilder() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ComponentFinder interceptorFinder = new ComponentFinder(InterceptorA.class, InterceptorB.class);
      ComponentFinder serviceFinder = new ComponentFinder(ServiceA.class, ServiceB.class, ServiceC.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver interceptorResolver = new MethodScannerResolver(scanner, interceptorFinder);
      MethodScannerResolver serviceResolver = new MethodScannerResolver(scanner, serviceFinder);
      ActionBuilder builder = new ActionBuilder(serviceResolver, interceptorResolver);

      // Test resolution on showA
      MockRequest request = new MockRequest("GET", "/a/b/c/show-a?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      Action action = builder.resolve(context);
      Object result = action.execute(context);

      assertNotNull(action);
      assertTrue(context.getValidation().isValid());
      assertEquals(context.getModel().getAttribute("a"), "A"); 
      assertEquals(context.getModel().getAttribute("b"), "B"); 
      assertEquals(context.getModel().getAttribute("method"), "showA");

      // Test resolution on showB
      MockRequest secondRequest = new MockRequest("GET", "/a/b/c/show-b?x=X&y=Y", "");
      MockResponse secondResponse = new MockResponse(System.out);
      Context secondContext = new HashContext(secondRequest, secondResponse);
      Action secondAction = builder.resolve(secondContext);
      Object secondResult = secondAction.execute(secondContext);

      assertNotNull(action);
      assertTrue(secondContext.getValidation().isValid());
      assertEquals(secondContext.getModel().getAttribute("a"), "A");
      assertEquals(secondContext.getModel().getAttribute("b"), "B"); 
      assertEquals(secondContext.getModel().getAttribute("method"), "showB");
                                                    // class

      // Test resolution on throwException
      MockRequest thirdRequest = new MockRequest("GET", "/a/bad/service/throw-exception?x=X&y=Y", "");
      MockResponse thirdResponse = new MockResponse(System.out);
      Context thirdContext = new HashContext(thirdRequest, thirdResponse);
      Action thirdAction = builder.resolve(thirdContext);
      Object thirdResult = thirdAction.execute(thirdContext);

      assertNotNull(action);
      assertTrue(thirdContext.getValidation().isValid());
      assertEquals(thirdContext.getModel().getAttribute("b"), "B"); 
      assertEquals(thirdContext.getError().getCause().getMessage(), "Catch me!!");

      // Test resolution on throwException with interpolation
      MockRequest fourthRequest = new MockRequest("GET", "/interpolate/exception/throw-exception?x=X&y=Y", "");
      MockResponse fourthResponse = new MockResponse(System.out);
      Context fourthContext = new HashContext(fourthRequest, fourthResponse);
      Action fourthAction = builder.resolve(fourthContext);
      Object fourthResult = fourthAction.execute(fourthContext);

      assertNotNull(action);
      assertTrue(fourthContext.getValidation().isValid());
      assertEquals(fourthContext.getModel().getAttribute("a"), null);
      assertEquals(fourthContext.getModel().getAttribute("b"), null); 
      assertEquals(fourthContext.getError().getCause().getMessage(), "Catch me!!"); 
   }

}
