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

public class StaticMethodCompletionTest extends TestCase {
   
   public void testStaticClassCompletion() throws Exception {
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
      
      CompletionRequest request = new CompletionRequest();
      
      request.setComplete("System.cu");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.tern");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("currentTimeMillis()"));
      assertEquals(completion.get("currentTimeMillis()"), "function");
      
      request = new CompletionRequest();
      
      request.setComplete("Runtime.g");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/other.tern");
      
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("getRuntime()"));
      assertEquals(completion.get("getRuntime()"), "function");
      
      request = new CompletionRequest();
      
      request.setComplete("Integer.");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/blah.tern");
      
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("MAX_VALUE"));
      assertNotNull(completion.get("MIN_VALUE"));
      assertNotNull(completion.get("valueOf(a)"));
      assertEquals(completion.get("MAX_VALUE"), "property");
      assertEquals(completion.get("MIN_VALUE"), "property");
      assertEquals(completion.get("valueOf(a)"), "function");
   }
}