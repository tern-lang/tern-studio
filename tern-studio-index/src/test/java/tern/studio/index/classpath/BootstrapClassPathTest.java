package tern.studio.index.classpath;

import java.util.Set;

import junit.framework.TestCase;

import tern.studio.index.IndexNode;
import tern.studio.index.config.SystemIndexConfigFile;

public class BootstrapClassPathTest extends TestCase {

   public void testScanner() throws Exception {
      Set<IndexNode> nodes = SystemIndexConfigFile.getSystemClassPath().getBootstrapClasses();
      
      for(IndexNode node : nodes) {
         String fullName = node.getFullName();
         System.err.println(fullName);
      }
   }
}
