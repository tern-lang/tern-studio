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

public class ImportCompletionTest extends TestCase {
   
   public void testImportCompletion() throws Exception {
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
      
      request.setComplete("import S");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/example.tern");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("lang.String"));
      assertNotNull(completion.get("lang.System"));
      assertEquals(completion.get("lang.String"), "class");
      assertEquals(completion.get("lang.System"), "class");
      
      request = new CompletionRequest();
      
      request.setComplete("import Hash");
      request.setSource("\nclass SomeClass{}\n");
      request.setLine(1);
      request.setResource("/other.tern");
      
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("util.concurrent.ConcurrentHashMap"));
      assertNotNull(completion.get("util.HashMap"));
      assertNotNull(completion.get("util.HashSet"));
      assertEquals(completion.get("util.concurrent.ConcurrentHashMap"), "class");
      assertEquals(completion.get("util.HashMap"), "class");
      assertEquals(completion.get("util.HashSet"), "class");
   }
   
   public void testImportCompletionForEmptySource() throws Exception {
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
      
      request.setComplete("import Has");
      request.setSource("");
      request.setLine(1);
      request.setResource("/example.tern");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("util.HashMap"));
      assertNotNull(completion.get("util.HashSet"));
      assertEquals(completion.get("util.HashMap"), "class");
      assertEquals(completion.get("util.HashSet"), "class");
   }
}
