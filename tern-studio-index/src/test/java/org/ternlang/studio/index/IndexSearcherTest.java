package org.ternlang.studio.index;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;

public class IndexSearcherTest extends TestCase {

   public void testIndexSearcher() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(Collections.EMPTY_LIST, context, pool, file, "test");
      SourceFile indexFile = database.getFile("/file.tern", "class X extends HashMap with Runnable {\nconst x = 0;\n}\n");
      IndexNode root = indexFile.getRootNode();
      String detail = IndexDumper.dump(root, root, "");
      
      System.err.println(detail);
      
      IndexNode node = indexFile.getNodeAtLine(2);
      Map<String, IndexNode> nodes = database.getNodesInScope(node);
      
      assertNotNull(nodes.get("x"));
      assertNotNull(nodes.get("run()"));
      assertNotNull(nodes.get("get(a)"));
      assertNotNull(nodes.get("put(a, b)"));
      assertNotNull(nodes.get("containsKey(a)"));
      assertEquals(nodes.get("x").getType(), IndexType.PROPERTY);
      assertEquals(nodes.get("run()").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("get(a)").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("put(a, b)").getType(), IndexType.MEMBER_FUNCTION);
      assertEquals(nodes.get("containsKey(a)").getType(), IndexType.MEMBER_FUNCTION);
   }
}
