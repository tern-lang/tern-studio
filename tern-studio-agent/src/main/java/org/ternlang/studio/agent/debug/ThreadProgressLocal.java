package org.ternlang.studio.agent.debug;

import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.ThreadProgress;

public class ThreadProgressLocal extends ThreadLocal<ThreadProgress> {
   
   private final BreakpointMatcher matcher;
   
   public ThreadProgressLocal(BreakpointMatcher matcher){
      this.matcher = matcher;
   }

   @Override
   protected ThreadProgress initialValue() {
      return new ThreadProgress(matcher);
   }
}