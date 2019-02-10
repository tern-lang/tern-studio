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

public class ThisChainCompletionTest extends TestCase {
   
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "this.stringBuilder.");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("append(a, b, c)"));
      assertNotNull(completion.get("append(a)"));
      assertNotNull(completion.get("setLength(a)"));
      assertEquals(completion.get("append(a, b, c)"), "function");
      assertEquals(completion.get("append(a)"), "function");
      assertEquals(completion.get("setLength(a)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "this.stringBuilder.s");
      response = compiler.completeExpression(request);
      completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("setLength(a)"));
      assertNotNull(completion.get("setCharAt(a, b)"));
      assertEquals(completion.get("setLength(a)"), "function");
      assertEquals(completion.get("setCharAt(a, b)"), "function");
   }
}
