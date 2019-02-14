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

public class ScriptFunctionCompletionTest extends TestCase {
   
   private static final String SOURCE_1 =
   "import util.regex.Pattern;\n"+
   "function fun(p: Pattern, s: String) {\n"+
   "   // replace me\n"+
   "}\n";

   private static final String SOURCE_2 =
   "import util.regex.Pattern;\n"+
   "function fun(p: Pattern, s: String)\n" +
   "{\n"+     
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_3 =
   "import util.regex.Pattern;\n"+
   "function fun(p: Pattern, s: String)\n" +
   "// some comment\n" +
   "{\n"+     
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_4 =
   "import util.regex.Pattern;\n"+
   "function fun(p: Pattern, s: String) { // comment\n"+
   "   // replace me\n"+
   "}\n";
   
   private static final String SOURCE_5 =
   "import util.regex.Pattern;\n"+
   "class Foo {\n"+
   "   fun(p: Pattern, s: String) {\n"+
   "      // replace me\n"+
   "   }\n"+
   "}";
   
   public void testScriptFunction() throws Exception {
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
      
      checkSource(compiler, SOURCE_1);
      checkSource(compiler, SOURCE_2);
      checkSource(compiler, SOURCE_3);
      checkSource(compiler, SOURCE_4);
      checkSource(compiler, SOURCE_5);
   }

   private void checkSource(CompletionCompiler compiler, String source) throws Exception {
      CompletionRequest request = SourceCodeInterpolator.buildRequest(source, "p.c");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("compile(a)"));
      assertNotNull(completion.get("compile(a, b)"));
      assertEquals(completion.get("compile(a)"), "function");
      assertEquals(completion.get("compile(a, b)"), "function");
   }
}
