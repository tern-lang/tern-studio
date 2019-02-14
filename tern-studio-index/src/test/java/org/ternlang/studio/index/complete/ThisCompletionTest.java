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

public class ThisCompletionTest extends TestCase {

   private static final String SOURCE =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      var blah: String = 'xx';\n"+
   "      if(length == 0) {\n"+
   "         // replace me\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
         
   public void testCompletionInForScope() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "this.a");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("append(source, offset, length)"));
      assertEquals(completion.get("append(source, offset, length)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "this.str");
      response = compiler.completeExpression(request);
      completion = response.getTokens();
      
      System.err.println(response.getDetails());

      assertNull(completion.get("source"));
      assertNotNull(completion.get("stringBuilder"));
      assertEquals(completion.get("stringBuilder"), "property");
      

   }
}
