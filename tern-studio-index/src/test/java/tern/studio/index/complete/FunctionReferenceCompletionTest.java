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

public class FunctionReferenceCompletionTest extends TestCase {
   
   public void testExtends() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class,
            FindInScopeMatching.class,
            FindPossibleImports.class,
            FindMethodReference.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest("// replace me", "list.stream().map(String::t");
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
   
      assertNotNull(completion.get("toUpperCase()"));
      assertNotNull(completion.get("toLowerCase()"));
      assertEquals(completion.get("toUpperCase()"), "function");
      assertEquals(completion.get("toLowerCase()"), "function");  
   }

}
