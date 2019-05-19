package org.ternlang.studio.resource.action.build;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.annotation.Attachment;
import org.ternlang.studio.resource.action.annotation.Path;
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
import org.ternlang.tree.function.ParameterExtractor;

import junit.framework.TestCase;

public class MethodHeaderInterpolationTest extends TestCase {
   
   @Path
   public static class ExampleObject {      
      
      @Path("/export")
      @Attachment("${token}.xls")
      public void exportSomeSpreadSheet(Response response) throws IOException {
        PrintStream out = response.getPrintStream();
        out.println("col1,col2,col3");
        out.println("1,2,3");
        out.println("a,b,c");
        out.close();
      }      
   }
   
   public void testInterpolatedHeader() throws Exception {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem();
      ComponentFinder finder = new ComponentFinder(ExampleObject.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/export?token=reportSpreadSheet", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);      
      dispatcher.execute(context);
      
      String header = response.getValue("Content-Disposition");      
      assertTrue(header.indexOf("reportSpreadSheet.xls") != -1);
   }

}

