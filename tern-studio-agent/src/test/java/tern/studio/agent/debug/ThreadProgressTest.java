package tern.studio.agent.debug;

import junit.framework.TestCase;

import tern.core.module.Path;
import tern.core.trace.Trace;
import tern.core.trace.TraceType;
import tern.studio.agent.debug.BreakpointMatcher;
import tern.studio.agent.debug.ThreadProgress;

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
