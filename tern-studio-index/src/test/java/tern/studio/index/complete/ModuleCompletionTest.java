package tern.studio.index.complete;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import tern.common.store.ClassPathStore;
import tern.common.thread.ThreadPool;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.studio.index.IndexDatabase;
import tern.studio.index.IndexScanner;
import tern.studio.index.config.SystemIndexConfigFile;

public class ModuleCompletionTest extends TestCase {
   
   private static final String SOURCE = 
   "module Constants {\n"+
   "   // replace me\n"+
   "   const MAX_VALUE = 10;\n"+
   "   const MIN_VALUE = 1;\n"+   
   "   const DEFAULT_NAME = \"blah\";\n"+
   "\n"+
   "   func(a, b) {\n"+
   "      return a+b;\n"+
   "   }\n"+
   "}\n";
         
   
   public void testClassCompletion() throws Exception {
      System.err.println(SOURCE);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "M");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("MAX_VALUE"));
      assertNotNull(completion.get("MIN_VALUE"));
      assertNotNull(completion.get("Map"));
      assertEquals(completion.get("MAX_VALUE"), "property");
      assertEquals(completion.get("MIN_VALUE"), "property");
      assertEquals(completion.get("Map"), "trait");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "");
      response = compiler.completeExpression(request);
      completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("MAX_VALUE"));
      assertNotNull(completion.get("MIN_VALUE"));
      assertNotNull(completion.get("Map"));
      assertNotNull(completion.get("func(a, b)"));
      assertEquals(completion.get("MAX_VALUE"), "property");
      assertEquals(completion.get("MIN_VALUE"), "property");
      assertEquals(completion.get("Map"), "trait");
      assertEquals(completion.get("func(a, b)"), "function");
   }
}