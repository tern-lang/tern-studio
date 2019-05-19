package org.ternlang.studio.resource.action.extract;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.annotation.Payload;
import org.ternlang.studio.resource.action.annotation.QueryParam;
import org.ternlang.studio.resource.action.build.ComponentBuilder;
import org.ternlang.studio.resource.action.build.ConstructorScanner;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
import org.ternlang.studio.resource.action.build.MockRequest;
import org.ternlang.studio.resource.action.build.MockResponse;

import junit.framework.TestCase;

public class NoArgComponentExtractorTest extends TestCase {

   @Payload
   public static class Query {

      public final String x;
      public final String y;

      public Query() {
         this("defaultX");
      }

      public Query(@QueryParam("x") String x) {
         this(x, "defaultY");
      }

      public Query(@QueryParam("x") String x, @QueryParam("y") String y) {
         this.x = x;
         this.y = y;
      }
   }

   public void testComponentExtractor() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem();
      ConstructorScanner scanner = new ConstructorScanner(dependencySystem, extractors);
      List<ComponentBuilder> builder = scanner.createBuilders(Query.class);
      ComponentExtractor extractor = new ComponentExtractor(builder, Query.class);
      Parameter parameter = new Parameter(Query.class, null, null, Collections.EMPTY_MAP, false);
      MockRequest request = new MockRequest("GET", "/?x=X", "");
      MockResponse response = new MockResponse();
      Context context = new HashContext(request, response);
      Query query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "X");
      assertEquals(query.y, "defaultY");

      request = new MockRequest("GET", "/", "");
      response = new MockResponse();
      context = new HashContext(request, response);
      query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, "defaultX");
      assertEquals(query.y, "defaultY");

      request = new MockRequest("GET", "/?y=Y", "");
      response = new MockResponse();
      context = new HashContext(request, response);
      query = (Query) extractor.extract(parameter, context);

      assertEquals(query.x, null);
      assertEquals(query.y, "Y");
   }

}
