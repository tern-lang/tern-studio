package org.ternlang.studio.index.classpath;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import junit.framework.TestCase;

public class ClassGraphTest extends TestCase {

   public void testClassGraph() throws Exception {
      ScanResult scanResult =
              new ClassGraph()
                      .enableAllInfo()
                      .disableDirScanning()
                      .enableSystemJarsAndModules()
                      .whitelistLibOrExtJars("rt.jar")
                      .scan();

      for(int i = 0; i < 1000; i++) {
         ClassInfo info = scanResult.getClassInfo("java.lang.String");
         System.err.println(info);
      }
   }
}
