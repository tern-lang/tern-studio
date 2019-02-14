package org.ternlang.studio.index;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.studio.index.config.SystemIndexConfigFile;

public class IndexerTest extends TestCase {

   private static final String SOURCE =
   "import lang.String;\n"+
   "import util.concurrent.ConcurrentHashMap;\n"+
   "import util.HashMap as Bag;\n"+
   "class SomeClass {\n"+
   "   var memb: SizeEnum = SizeEnum.BIG;\n"+
   "   test(index, size): Mod.ModClass {\n"+
   "     var str = 'xxx';\n"+
   "     println(str);\n"+
   "   }\n"+      
   "   class InnerClass{}\n"+
   "}\n"+
   "enum SizeEnum{\n"+
   "   BIG,\n"+
   "   SMALL,\n"+
   "   TINY;\n"+
   "   func(){\n"+
   "      var a = 1;\n"+
   "      var b = 2;\n"+
   "      var c = 3;\n"+
   "      return a+b+c;\n"+
   "   }\n"+
   "}\n"+
   "module Mod {\n"+
   "   class ModClass{\n"+
   "      const PROP = 'abc';\n"+
   "   }\n"+
   "   trait ModTrait{\n"+
   "      someTraitFunc(a,b,c);\n"+
   "   }\n"+
   "   modFunc(){}\n"+
   "}\n"+
   "const PI = 3.14;\n"+
   "var x =10;\n"+
   "function foo(a, b) {\n"+
   "   if(x > 0){\n"+
   "      var y = x;\n"+
   "      y++;\n"+
   "   }\n"+
   "   if(x  >0){\n"+
   "      var y = 0;\n"+
   "      y++;\n"+
   "   }\n"+
   "   if(x !=77){\n"+
   "      if(x > 0){\n"+
   "         var y =1;\n"+
   "         y--;\n"+
   "      } else {\n"+
   "         var y=33;\n"+
   "         y++;\n"+
   "      }\n"+
   "   }\n"+
   "}\n"+
   "class Blah {\n"+
   "   const text: String;\n"+
   "   new(text){\n"+
   "      this.text = text;\n"+
   "   }\n"+
   "   public doSomething(x): String{\n"+
   "   }\n"+
   "}\n";
   
   public void testDefaultConstructors() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      PathTranslator translator = new PathTranslator();
      SourceIndexer indexer = new SourceIndexer(translator, database, context, pool, null);
      SourceFile searcher = indexer.index("/some/package.tern", SOURCE);
      Map<String, IndexNode> nodes = searcher.getNodesInScope(5);
   
      assertNotNull(nodes.get("SomeClass()"));
      assertEquals(nodes.get("SomeClass()").getType(), IndexType.CONSTRUCTOR);
   }
   
   public void testTypeNodes() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      PathTranslator translator = new PathTranslator();
      SourceIndexer indexer = new SourceIndexer(translator, database, context, pool, null);
      SourceFile searcher = indexer.index("/some/package.tern", SOURCE);
      Map<String, IndexNode> nodes = searcher.getTypeNodes();
      
      assertNotNull(nodes.get("String"));
      assertNotNull(nodes.get("ConcurrentHashMap"));
      assertNotNull(nodes.get("Bag"));
      assertNotNull(nodes.get("SomeClass"));
      assertNotNull(nodes.get("SomeClass.InnerClass"));
      assertNotNull(nodes.get("SizeEnum"));
      assertNotNull(nodes.get("Mod"));
      assertNotNull(nodes.get("Mod.ModClass"));
      assertNotNull(nodes.get("Mod.ModTrait"));
      
      assertEquals(nodes.get("String").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("ConcurrentHashMap").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("Bag").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("SomeClass").getType(), IndexType.CLASS);
      assertEquals(nodes.get("SomeClass.InnerClass").getType(), IndexType.CLASS);
      assertEquals(nodes.get("SizeEnum").getType(), IndexType.ENUM);
      assertEquals(nodes.get("Mod").getType(), IndexType.MODULE);
      assertEquals(nodes.get("Mod.ModClass").getType(), IndexType.CLASS);
      assertEquals(nodes.get("Mod.ModTrait").getType(), IndexType.TRAIT);
   }
   
   public void testNodesInScope() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      PathTranslator translator = new PathTranslator();
      SourceIndexer indexer = new SourceIndexer(translator, database, context, pool, null);
      SourceFile searcher = indexer.index("/some/package.tern", SOURCE);
      Map<String, IndexNode> nodes = searcher.getNodesInScope(6);
      
      assertNotNull(nodes.get("String"));
      assertNotNull(nodes.get("ConcurrentHashMap"));
      assertNotNull(nodes.get("SomeClass"));
      assertNotNull(nodes.get("test(index, size)"));
      assertNotNull(nodes.get("memb"));
      assertNotNull(nodes.get("str"));
      assertNotNull(nodes.get("InnerClass"));
      assertNotNull(nodes.get("SizeEnum"));
      assertNotNull(nodes.get("Mod"));
      
      assertEquals(nodes.get("String").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("String").getFullName(), "lang.String");
      assertEquals(nodes.get("ConcurrentHashMap").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("ConcurrentHashMap").getFullName(), "util.concurrent.ConcurrentHashMap");
      assertEquals(nodes.get("Bag").getType(), IndexType.IMPORT);
      assertEquals(nodes.get("Bag").getFullName(), "util.HashMap");
      assertEquals(nodes.get("SomeClass").getType(), IndexType.CLASS);
      assertEquals(nodes.get("SomeClass").getFullName(), "some.package.SomeClass");
      assertEquals(nodes.get("test(index, size)").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("test(index, size)").getConstraint().getTypeName(), "Mod.ModClass");
      assertEquals(nodes.get("memb").getType(), IndexType.PROPERTY);
      assertEquals(nodes.get("memb").getConstraint().getTypeName(), "SizeEnum");
      assertEquals(nodes.get("str").getType(), IndexType.VARIABLE);
      assertEquals(nodes.get("InnerClass").getType(), IndexType.CLASS);
      assertEquals(nodes.get("InnerClass").getFullName(), "some.package.SomeClass.InnerClass");
      assertEquals(nodes.get("SizeEnum").getType(), IndexType.ENUM);
      assertEquals(nodes.get("Mod").getType(), IndexType.MODULE);
   }
   
   public void testNodeSearch() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      PathTranslator translator = new PathTranslator();
      SourceIndexer indexer = new SourceIndexer(translator, database, context, pool, null);
      SourceFile searcher = indexer.index("/some/package.tern", SOURCE);
      IndexNode node = searcher.getRootNode();
      
      System.err.println(IndexDumper.dump(node, node, ""));
      
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(4), 1);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(7), 2);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(13), 1);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(32), 0);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(25), 2);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(45), 3);
      assertEquals(((IndexSearcher)searcher).getDepthAtLine(56), 2);
      
      assertEquals(searcher.getNodeAtLine(4).getType(), IndexType.CONSTRUCTOR);
      assertEquals(searcher.getNodeAtLine(4).getName(), "SomeClass()");
      assertEquals(searcher.getNodeAtLine(4).getFullName(), "SomeClass()");
      assertEquals(searcher.getNodeAtLine(7).getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(searcher.getNodeAtLine(7).getName(), "test(index, size)");
      assertEquals(searcher.getNodeAtLine(7).getConstraint().getTypeName(), "Mod.ModClass");
      assertEquals(searcher.getNodeAtLine(13).getType(), IndexType.ENUM);
      assertEquals(searcher.getNodeAtLine(13).getName(), "SizeEnum");
      assertEquals(searcher.getNodeAtLine(13).getFullName(), "some.package.SizeEnum");
      assertEquals(searcher.getNodeAtLine(32).getType(), IndexType.SCRIPT);
      assertEquals(searcher.getNodeAtLine(32).getName(), "/some/package.tern");
      assertEquals(searcher.getNodeAtLine(25).getType(), IndexType.CONSTRUCTOR);
      assertEquals(searcher.getNodeAtLine(25).getName(), "ModClass()");
      assertEquals(searcher.getNodeAtLine(25).getFullName(), "ModClass()");      
      assertEquals(searcher.getNodeAtLine(45).getType(), IndexType.COMPOUND);
      assertEquals(searcher.getNodeAtLine(45).getName(), "{}");
      assertEquals(searcher.getNodeAtLine(56).getType(), IndexType.CONSTRUCTOR);
      assertEquals(searcher.getNodeAtLine(56).getName(), "Blah(text)");
   }
}
