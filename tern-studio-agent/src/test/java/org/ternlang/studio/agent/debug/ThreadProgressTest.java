package org.ternlang.studio.agent.debug;

import junit.framework.TestCase;

import org.ternlang.core.module.Path;
import org.ternlang.core.trace.Trace;
import org.ternlang.core.trace.TraceType;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.ThreadProgress;

public class ThreadProgressTest extends TestCase {
   
   public void testThreadProgress() throws Exception {
      BreakpointMatcher matcher = new BreakpointMatcher();
      ThreadProgress progress = new ThreadProgress(matcher);
      
//      progress.beforeInstruction(Trace.INVOKE);
//      progress.isSuspendBefore(createNormalTrace("x", 1));
   }

   private static Trace createNormalTrace(String resource, int line) {
      Path path = new Path(resource);
      return new Trace(TraceType.NORMAL, null, path, line);
   }
}
