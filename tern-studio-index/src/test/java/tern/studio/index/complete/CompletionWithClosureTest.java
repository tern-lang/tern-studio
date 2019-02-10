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

public class CompletionWithClosureTest extends TestCase {

   private static final String SOURCE =
   "import util.regex.Pattern;\n"+      
   "class Foo {\n"+
   "   func(l: List){\n"+
   "      return l.stream()\n"+
   "         .filter(x -> {\n"+
   "            const a: String = null;\n"+
   "            const b: Integer = null;\n"+
   "            const c: Pattern = null;\n"+   
   "            \n"+
   "            // replace me\n"+
   "            return x.y;\n"+
   "          })\n"+
   "         .findFirst()\n"+
   "         .get();\n"+
   "   }\n"+
   "}\n";

   public void testWithClosure() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "c.c");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("compile(a)"));
      assertNotNull(completion.get("compile(a)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "a.s");
      response = compiler.completeExpression(request);
      completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertNotNull(completion.get("substring(a)"), "function");
      assertNotNull(completion.get("substring(a, b)"), "function");
   }

}
