package org.ternlang.studio.resource.action.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.Inject;
import org.ternlang.studio.resource.action.annotation.Intercept;
import org.ternlang.studio.resource.action.annotation.QueryParam;
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

public class MethodResolverOrderTest extends TestCase {

   @Intercept("/a/b/c")
   public static class Longest {
      @QueryParam("x")
      String x;
      String y;

      public Longest(@QueryParam("y") String y) {
         this.y = y;
      }

      Response response;

      @Intercept("/.*")
      public void fun(Model model) {
         List list = (List) model.getAttribute("list");
         list.add(this);
      }
   }

   @Intercept("/a/b")
   public static class Middle {
      @Inject
      Request request;
      @Inject
      Response response;

      @Intercept("/.*")
      public void fun(Model model) {
         List list = (List) model.getAttribute("list");
         list.add(this);
      }
   }

   @Intercept("/a")
   public static class Shortest {
      @Inject
      Request request;
      Response response;

      @Intercept("/.*")
      public void fun(Model model) {
         List list = (List) model.getAttribute("list");
         list.add(this);
      }
   }

   @Intercept("/")
   public static class AlsoShortest {
      @Inject
      Request request;

      @Intercept("/a")
      public void fun(Model model) {
         List list = (List) model.getAttribute("list");
         list.add(this);
      }
   }

   public void testOrder() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem();
      ComponentFinder finder = new ComponentFinder(AlsoShortest.class, Longest.class, Middle.class, Shortest.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/a/b/c/d/blah?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      List list = new ArrayList();
      context.getModel().setAttribute("list", list);

      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertEquals(list.size(), 1);
      assertEquals(list.get(0).getClass(), Longest.class);

      list.clear();
      assertTrue(list.isEmpty());

      List<MethodDispatcher> bestFirst = resolver.resolveBestFirst(context);

      assertTrue(list.isEmpty());
      assertFalse(bestFirst.isEmpty());
      assertEquals(bestFirst.size(), 3);

      for (MethodDispatcher entry : bestFirst) {
         entry.execute(context);
      }
      assertFalse(list.isEmpty());
      assertEquals(list.size(), 3);
      assertEquals(list.get(0).getClass(), Longest.class);
      assertEquals(list.get(1).getClass(), Middle.class);
      assertEquals(list.get(2).getClass(), Shortest.class);

      list.clear();
      assertTrue(list.isEmpty());

      List<MethodDispatcher> bestLast = resolver.resolveBestLast(context);

      assertTrue(list.isEmpty());
      assertFalse(bestLast.isEmpty());
      assertEquals(bestLast.size(), 3);

      for (MethodDispatcher entry : bestLast) {
         entry.execute(context);
      }
      assertFalse(list.isEmpty());
      assertEquals(list.size(), 3);
      assertEquals(list.get(0).getClass(), Shortest.class);
      assertEquals(list.get(1).getClass(), Middle.class);
      assertEquals(list.get(2).getClass(), Longest.class);

      assertFalse(list.isEmpty());
      assertEquals(list.size(), 3);
      assertEquals(((Shortest) list.get(0)).request, request);
      assertEquals(((Shortest) list.get(0)).response, null);
      assertEquals(((Middle) list.get(1)).request, request);
      assertEquals(((Middle) list.get(1)).response, response);
      assertEquals(((Longest) list.get(2)).response, null);
      assertEquals(((Longest) list.get(2)).x, "X");
      assertEquals(((Longest) list.get(2)).y, "Y");
   }

}
