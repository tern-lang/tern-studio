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

public class CompletionCompilerTest extends TestCase {

   private static final String SOURCE = 
   "class SomePath {\n"+
   "   var memb1: TypeEnum;\n"+
   "   var memb2: InnerClass;\n"+
   "   findSomething(index){\n"+
   "      // replace me\n"+
   "   }\n"+
   "   doSomething(index) {\n"+
   "      return 'foo'+index;\n"+
   "   }\n"+
   "   doSomething(){\n"+
   "      return 'foo';\n"+
   "   }\n"+
   "   class InnerClass {\n"+
   "      var x: String;\n"+
   "      var length: Integer;\n"+
   "      new(x, length){\n"+
   "         this.x = x;\n"+
   "         this.length = length;\n"+
   "      }\n"+
   "      someInnerFunc(): TypeEnum{\n"+
   "         return TypeEnum.ONE;\n"+
   "      }\n"+
   "   }\n"+
   "}\n"+
   "enum TypeEnum{\n"+
   "   ONE,\n"+
   "   TWO,\n"+
   "   THREE;\n"+
   "   at(index){\n"+
   "      return values[index];\n"+
   "   }\n"+
   "}\n";

   
   public void testCompletionCompiler() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
      CompletionCompiler compiler = new CompletionCompiler(database, 
            FindForExpression.class,
            FindInScopeMatching.class,
            FindConstructorsInScope.class,
            FindPossibleImports.class);
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE, "do");
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("doSomething()"));
      assertNotNull(completion.get("doSomething(index)"));
      assertEquals(completion.get("doSomething()"), "function");
      assertEquals(completion.get("doSomething(index)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "memb");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("memb1"));
      assertNotNull(completion.get("memb2"));
      assertEquals(completion.get("memb1"), "property");
      assertEquals(completion.get("memb2"), "property");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "memb1");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("memb1"));
      assertNull(completion.get("memb2"));
      assertEquals(completion.get("memb1"), "property");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "InnerClass.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("x"));
      assertNotNull(completion.get("length"));
      assertNotNull(completion.get("someInnerFunc()"));
      assertEquals(completion.get("x"), "property");
      assertEquals(completion.get("length"), "property");
      assertEquals(completion.get("someInnerFunc()"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "InnerClass.l");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNull(completion.get("x"));
      assertNotNull(completion.get("length"));
      assertNull(completion.get("someInnerFunc()"));
      assertEquals(completion.get("length"), "property");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("SomePath"));
      assertNotNull(completion.get("memb1"));
      assertNotNull(completion.get("memb2"));
      assertNotNull(completion.get("findSomething(index)"));
      assertNotNull(completion.get("doSomething(index)"));
      assertNotNull(completion.get("doSomething()"));
      assertNotNull(completion.get("InnerClass"));
      assertNotNull(completion.get("TypeEnum"));
      assertEquals(completion.get("SomePath"), "class");
      assertEquals(completion.get("memb1"), "property");
      assertEquals(completion.get("memb2"), "property");
      assertEquals(completion.get("findSomething(index)"), "function");
      assertEquals(completion.get("doSomething(index)"), "function");
      assertEquals(completion.get("doSomething()"), "function");
      assertEquals(completion.get("InnerClass"), "class");
      assertEquals(completion.get("TypeEnum"), "enum");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE, "new ");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("InnerClass(x, length)"));
      assertEquals(completion.get("InnerClass(x, length)"), "constructor");
   }
}
