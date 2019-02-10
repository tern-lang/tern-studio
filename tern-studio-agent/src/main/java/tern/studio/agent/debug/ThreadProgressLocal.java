package tern.studio.agent.debug;

import tern.studio.agent.debug.BreakpointMatcher;
import tern.studio.agent.debug.ThreadProgress;

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