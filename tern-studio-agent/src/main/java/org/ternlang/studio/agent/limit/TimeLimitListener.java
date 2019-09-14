package org.ternlang.studio.agent.limit;

import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.trace.Trace;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.debug.TraceAdapter;

public class TimeLimitListener extends TraceAdapter {
   
   private final LimitExpiredTrigger trigger;
   private final ThreadBuilder builder;
   private final AtomicBoolean expired;
   private final AtomicBoolean started;
   
   public TimeLimitListener(long duration) {
      this.expired = new AtomicBoolean();
      this.trigger = new LimitExpiredTrigger(expired, duration);
      this.builder = new ThreadBuilder();
      this.started = new AtomicBoolean();
   }
   
   @Override
   public void traceBefore(Scope scope, Trace trace) {
      if(started.compareAndSet(false, true)) {
         Thread thread = builder.newThread(trigger);
         thread.start();
      }
      if(expired.get()) {
         TerminateHandler.terminate("Time limit expired");
      }
   }
   
   @Override
   public void traceAfter(Scope scope, Trace trace) {
      if(expired.get()) {
         TerminateHandler.terminate("Time limit expired");
      }
   }
   
   private static class LimitExpiredTrigger implements Runnable {
      
      private final AtomicBoolean expired;
      private final long expireTime;
      
      public LimitExpiredTrigger(AtomicBoolean expired, long duration) {
         this.expireTime = System.currentTimeMillis() + duration;
         this.expired = expired;
      }
      
      @Override
      public void run() {
         try {
            while(true) {
               long currentTime = System.currentTimeMillis();
            
               if(currentTime >= expireTime) {
                  expired.set(true);
                  break;
               }
               Thread.sleep(1000);
            }
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }

}
