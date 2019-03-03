package org.ternlang.studio.index;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;
import org.ternlang.common.store.ClassPathStore;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;

public class IndexScannerTest extends TestCase {
   
   public void testScanner() throws Exception {
      UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
         
         @Override
         public void uncaughtException(Thread thread, Throwable cause) {
            cause.printStackTrace();
         }
      };
      Thread.setDefaultUncaughtExceptionHandler(handler);
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      File root = new File("c:/Work/development/ternlang/snap-develop/snap-studio/work/games");
      ThreadPool pool = new ThreadPool(1);
      IndexScanner scanner = new IndexScanner(Collections.EMPTY_LIST, context, pool, root, "demo", "mario/src", "mario/assets");
      long start = System.currentTimeMillis();
      Map<String, IndexNode> nodes = scanner.getTypeNodesMatching(".*");
      long finish = System.currentTimeMillis();
      System.err.println("time="+(finish-start));
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      
      for(Entry<String, IndexNode> entry : entries) {
         String name = entry.getKey();
         IndexNode node = entry.getValue();
         String fullName = node.getFullName();
         
         System.err.println(fullName);
      }
   }

}
