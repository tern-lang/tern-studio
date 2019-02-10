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

public class NewInstanceCompletionTest extends TestCase {
   
   public void testNewInstance() throws Exception {
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
      
      request.setComplete("new S");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.tern");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("SomeClass()"));
      assertNotNull(completion.get("StringBuilder()"));
      assertNotNull(completion.get("StringBuilder(a)"));
      assertEquals(completion.get("SomeClass()"), "constructor");
      assertEquals(completion.get("StringBuilder()"), "constructor");
      assertEquals(completion.get("StringBuilder(a)"), "constructor");
   }
}