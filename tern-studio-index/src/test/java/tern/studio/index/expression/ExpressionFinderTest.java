package tern.studio.index.expression;

import java.io.File;
import java.util.Set;

import junit.framework.TestCase;

import tern.common.store.ClassPathStore;
import tern.common.thread.ThreadPool;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.studio.index.IndexDatabase;
import tern.studio.index.IndexDumper;
import tern.studio.index.IndexNode;
import tern.studio.index.IndexScanner;
import tern.studio.index.SourceFile;
import tern.studio.index.config.SystemIndexConfigFile;

public class ExpressionFinderTest extends TestCase {
   
   private static final String SOURCE_1 = 
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

   public void testExpressionFinder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      ExpressionFinder finder = new ExpressionFinder(database);
      SourceFile indexFile = database.getFile("/test.tern", SOURCE_1);
      IndexNode nodeAtLine = indexFile.getNodeAtLine(5);
      IndexNode root = indexFile.getRootNode();
      String details = IndexDumper.dump(root, nodeAtLine, "memb2.someInnerFunc().a");
      Set<IndexNode> matched = finder.find(nodeAtLine, "memb2.someInnerFunc().a");
      
      System.err.println(details);
      assertNotNull(matched);
   }
}
