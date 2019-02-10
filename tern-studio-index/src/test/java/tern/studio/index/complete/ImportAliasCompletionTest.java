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

public class ImportAliasCompletionTest extends TestCase {

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
      
      request.setComplete("Au");
      request.setSource("import sound.sampled.AudioFormat;\n\n"); // this is an alias for javax.sound.sampled.AudioFormat
      request.setLine(2);
      request.setResource("/example.snap");
      
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("AudioFormat"));
      assertNotNull(completion.get("AutoCloseable"));
      assertEquals(completion.get("AudioFormat"), "class");
      assertEquals(completion.get("AutoCloseable"), "trait");
   }
}
