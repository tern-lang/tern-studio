package org.ternlang.studio.index.complete;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexScanner;

public class ExtendedTypeCompletionTest extends TestCase {
   
   private static final String SOURCE_1 = 
   "class Foo extends HashMap with Runnable {\n"+
   "   const x = 11;\n"+
   "   blah(){\n"+
   "      // replace me\n"+
   "      println(x);\n"+
   "   }\n"+
   "}\n";  
   
   private static final String SOURCE_2 =
   "try {\n"+
   "   throw new Exception();\n"+
   "}catch(e: Exception) {\n"+
   "   println(x);\n"+
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_3 =
   "try {\n"+
   "   throw new Exception();\n"+
   "}catch(e: String) {\n"+
   "   println(e);\n"+
   "   // replace me\n"+
   "}\n";
   
   public void testExceptionWithStringCompletion() throws Exception {
      System.err.println(SOURCE_3);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_3, "e.");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
   
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("substring(a, b)"), "function");
   }
   
   public void testExceptionCompletion() throws Exception {
      System.err.println(SOURCE_2);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_2, "e.");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
   
      assertNotNull(completion.get("printStackTrace()"));
      assertNotNull(completion.get("printStackTrace(a)"));
      assertNotNull(completion.get("getStackTrace()"));
      assertEquals(completion.get("printStackTrace()"), "function");
      assertEquals(completion.get("printStackTrace(a)"), "function");
      assertEquals(completion.get("getStackTrace()"), "function");
   }
   
   
   public void testExtendedClassCompletion() throws Exception {
      System.err.println(SOURCE_1);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_1, "");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());

      assertNotNull(completion.get("x"));
      assertNotNull(completion.get("blah()"));
      assertNotNull(completion.get("run()"));
      assertNotNull(completion.get("get(a)"));
      assertNotNull(completion.get("put(a, b)"));
      assertNotNull(completion.get("containsKey(a)"));
      assertEquals(completion.get("x"), "property");
      assertEquals(completion.get("blah()"), "function");
      assertEquals(completion.get("run()"), "function");
      assertEquals(completion.get("get(a)"), "function");
      assertEquals(completion.get("put(a, b)"), "function");
      assertEquals(completion.get("containsKey(a)"), "function");
   }
}