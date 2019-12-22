package org.ternlang.studio;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

import com.sun.management.ThreadMXBean;

import junit.framework.TestCase;

public abstract class PerfTestCase extends TestCase {

   public void timeRun(String name, Runnable task) throws Exception {
      DecimalFormat format = new DecimalFormat("###,###,###,###,###");
      ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
      Thread thread = Thread.currentThread();
      long id = thread.getId();
      System.gc();
      System.gc();
      Thread.sleep(100);
      long before = bean.getThreadAllocatedBytes(id);
      long start = System.currentTimeMillis();
      task.run();
      long finish = System.currentTimeMillis();
      long after = bean.getThreadAllocatedBytes(id);
      System.out.println();
      System.out.println(name + ": time=" + (finish - start) + " memory=" + format.format(after - before));
   }
}
