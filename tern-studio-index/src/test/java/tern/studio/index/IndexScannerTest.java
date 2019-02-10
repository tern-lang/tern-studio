package tern.studio.index;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;

import tern.common.store.ClassPathStore;
import tern.common.thread.ThreadPool;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.studio.index.config.SystemIndexConfigFile;

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
      File root = new File("c:/Work/development/snapscript/snap-develop/snap-studio/work/games");
      ThreadPool pool = new ThreadPool(1);
      IndexScanner scanner = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, root, "demo", "mario/src", "mario/assets");
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
