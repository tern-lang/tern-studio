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

public class CompletionInCompoundScopeTest extends TestCase {

   private static final String SOURCE_1 =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      var blah: String = 'xx';\n"+
   "      if(length == 0) {\n"+
   "         // replace me\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
   
   private static final String SOURCE_2 =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      var blah: String = 'xx';\n"+
   "      try {\n"+
   "         var y: Long = 11l;\n"+
   "         var z = 12;\n"+
   "         // replace me\n"+
   "         source = source.substring(1);\n"+
   "      } catch(e) {\n"+
   "         var m = e.getMessage();\n"+
   "         e.printStackTrace();\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
   
   private static final String SOURCE_3 =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      var blah: String = 'xx';\n"+
   "      try {\n"+
   "         source = source.substring(1);\n"+
   "      } catch(e: IllegalStateException) {\n"+
   "         // replace me\n"+
   "         e.printStackTrace();\n"+
   "      }\n"+
   "   }\n"+
   "}\n"; 
   
   private static final String SOURCE_4 =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) {\n"+
   "      var blah: String = 'xx';\n"+
   "      for(var idx = 0; idx < length; idx++){\n"+
   "         source = source.substring(idx);\n"+
   "         // replace me\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
         
   public void testCompletionInForScope() throws Exception {
      System.err.println(SOURCE_4);
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_4, "id");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      System.err.println(response.getDetails());
      
      assertNotNull(completion.get("idx"));
      assertEquals(completion.get("idx"), "variable");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "source.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("substring(a, b)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "appe");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("append(source, offset, length)"));
      assertEquals(completion.get("append(source, offset, length)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "b");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("blah"));
      assertEquals(completion.get("blah"), "variable");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "blah.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("getBytes(a)"), "function");
   }
   
   public void testCompletionInCatchScope() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_3, "e.");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      //System.err.println(response.getDetails());
      
      assertNotNull(completion.get("printStackTrace()"));
      assertNotNull(completion.get("printStackTrace(a)"));
      assertEquals(completion.get("printStackTrace()"), "function");
      assertEquals(completion.get("printStackTrace(a)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "source.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("substring(a, b)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "appe");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("append(source, offset, length)"));
      assertEquals(completion.get("append(source, offset, length)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "b");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("blah"));
      assertEquals(completion.get("blah"), "variable");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_3, "blah.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("getBytes(a)"), "function");
   }
   
   public void testCompletionInTryScope() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_2, "s");
      CompletionResponse response = compiler.completeExpression(request);
      Map<String, String> completion = response.getTokens();
      
      //System.err.println(response.getDetails());
      
      assertNotNull(completion.get("source"));
      assertNotNull(completion.get("stringBuilder"));
      assertEquals(completion.get("stringBuilder"), "property");
      assertEquals(completion.get("source"), "parameter");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_2, "source.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("substring(a, b)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_2, "y");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("y"));
      assertEquals(completion.get("y"), "variable");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_2, "y.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("valueOf(a)"));
      assertEquals(completion.get("valueOf(a)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_2, "appe");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("append(source, offset, length)"));
      assertEquals(completion.get("append(source, offset, length)"), "function");
   }
         
   public void testCompletionInIfScope() throws Exception {
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
      
      CompletionRequest request = SourceCodeInterpolator.buildRequest(SOURCE_1, "s");
      Map<String, String> completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("source"));
      assertNotNull(completion.get("stringBuilder"));
      assertEquals(completion.get("stringBuilder"), "property");
      assertEquals(completion.get("source"), "parameter");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_1, "source.");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("substring(a)"));
      assertNotNull(completion.get("substring(a, b)"));
      assertEquals(completion.get("substring(a)"), "function");
      assertEquals(completion.get("substring(a, b)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_1, "appe");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("append(source, offset, length)"));
      assertEquals(completion.get("append(source, offset, length)"), "function");
      
      request = SourceCodeInterpolator.buildRequest(SOURCE_1, "bl");
      completion = compiler.completeExpression(request).getTokens();
      
      assertNotNull(completion.get("blah"));
      assertEquals(completion.get("blah"), "variable");
   }
}
