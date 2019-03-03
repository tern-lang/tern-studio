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

public class FunctionReferenceCompletionTest extends TestCase {
   
   public void testExtends() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
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
