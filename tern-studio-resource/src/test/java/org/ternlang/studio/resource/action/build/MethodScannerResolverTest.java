package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.annotation.CookieParam;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.Produces;
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

public class MethodScannerResolverTest extends TestCase {

   @Path("/")
   private static class ExampleController {

      @Path("/listPeople")
      public void listPeople(
            @QueryParam("person") String person, 
            @CookieParam("SSOID") String id, 
            Model model) 
      {
         System.err.printf("%s %s %s", person, id, model);
      }
   }

   @Path("/contextPath/")
   private static class ExampleCompositeController {

      @Produces("text/plain")
      @Path
      public void listPeople(
            CompositeParam param, 
            @QueryParam("person") String person, 
            Model model) 
      {
         System.err.printf("%s %s %s %s", param.person, param.id, person, model);
      }

      @Produces("text/plain")
      @Path
      public void listPeople(
            CompositeParam param, 
            @QueryParam("person") String person, 
            @Required @QueryParam("enum") SomeEnum x, 
            Model model) 
      {
         System.err.printf("%s %s %s %s %s", param.person, param.id, person, x, model);
      }
   }

   private static enum SomeEnum {
      X, Y, Z;
   }

   @Component
   private static class CompositeParam {

      public final String person;
      public final String id;

      public CompositeParam(
            @Required @QueryParam("person") String person, 
            @Required @CookieParam("SSOID") String id) 
      {
         this.person = person;
         this.id = id;
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
      ComponentFinder finder = new ComponentFinder(ExampleCompositeController.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/context-path/list-people?person=niall.gallagher@rbs.com&enum=X", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);
   }

   public void testSimpleController() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ComponentFinder finder = new ComponentFinder(ExampleController.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/list-people?person=niall.gallagher@rbs.com", "");
      request.setCookie("SSOID", "XYZ");
      request.setCookie("SSOSESSION", "ABC");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);
   }
}
