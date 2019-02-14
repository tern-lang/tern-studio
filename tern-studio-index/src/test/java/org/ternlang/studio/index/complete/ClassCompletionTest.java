package org.ternlang.studio.index.complete;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexScanner;
import org.ternlang.studio.index.config.SystemIndexConfigFile;

public class ClassCompletionTest extends TestCase {
   
   public void testClassCompletion() throws Exception {
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
      
      CompletionRequest request = new CompletionRequest();
      
      request.setComplete("S");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.tern");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("String"));
      assertNotNull(completion.get("System"));
      assertNotNull(completion.get("SomeClass"));
      assertEquals(completion.get("String"), "class");
      assertEquals(completion.get("System"), "class");
      assertEquals(completion.get("SomeClass"), "class");
      
      request = new CompletionRequest();
      
      request.setComplete("Hash");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/other.tern");
      
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("HashMap"));
      assertNotNull(completion.get("HashSet"));
      assertEquals(completion.get("HashMap"), "class");
      assertEquals(completion.get("HashSet"), "class");
   }
}