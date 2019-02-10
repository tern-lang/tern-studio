package tern.studio.index.complete;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import tern.common.store.ClassPathStore;
import tern.common.thread.ThreadPool;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.studio.index.IndexDatabase;
import tern.studio.index.IndexNode;
import tern.studio.index.IndexScanner;
import tern.studio.index.IndexType;
import tern.studio.index.SourceFile;
import tern.studio.index.config.SystemIndexConfigFile;

public class ImportAliasTest extends TestCase {
   
   public void testImportAlias() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ThreadPool pool = new ThreadPool(2);
      File file = File.createTempFile("test", getClass().getSimpleName());
      IndexDatabase database = new IndexScanner(SystemIndexConfigFile.getSystemClassPath(), context, pool, file, "test");
      SourceFile resource = database.getFile("/test.tern", "import sound.sampled.AudioFormat;");
      IndexNode root = resource.getRootNode();
      Map<String, IndexNode> nodes = database.getNodesInScope(root);
      
      assertNotNull(nodes.get("AudioFormat"));
      assertEquals(nodes.get("AudioFormat").getType(), IndexType.IMPORT);
      
      String fullName = nodes.get("AudioFormat").getFullName();
      IndexNode node = database.getTypeNode(fullName);
      
      assertNotNull(node);
      assertEquals(node.getType(), IndexType.CLASS);
      assertEquals(node.getFullName(), "javax.sound.sampled.AudioFormat");
   }

}
