package org.ternlang.studio.agent.limit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.trace.Trace;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.debug.TraceAdapter;

public class TimeLimiter extends TraceAdapter {
   
   private final LimitExpiredTrigger trigger;
   private final ThreadBuilder builder;
   private final AtomicBoolean expired;
   private final AtomicBoolean started;
   private final AtomicLong expiry;
   private final AtomicLong limit;
   
   public TimeLimiter() {
      this.limit = new AtomicLong();
      this.expiry = new AtomicLong();
      this.expired = new AtomicBoolean();
      this.trigger = new LimitExpiredTrigger(expiry, expired);
      this.builder = new ThreadBuilder();
      this.started = new AtomicBoolean();
   }
   
   public TimeLimit getTimeLimit() {
      long duration = limit.get();
      long expiryTime = expiry.get();
      
      return new TimeLimit(expiryTime, duration);
   }
   
   public void expireAfter(long duration) {
      long time = System.currentTimeMillis();
      
      if(started.compareAndSet(false, true)) {
         Thread thread = builder.newThread(trigger);
         thread.start();
      }
      limit.set(duration);
      expiry.set(time + duration);
   }
   
   @Override
   public void traceBefore(Scope scope, Trace trace) {
      if(expired.get()) {
         TerminateHandler.terminate("Time limit expired");
      }
   }

   private static class LimitExpiredTrigger implements Runnable {
      
      private final AtomicBoolean expired;
      private final AtomicLong expiry;
      
      public LimitExpiredTrigger(AtomicLong expiry, AtomicBoolean expired) {
         this.expired = expired;
         this.expiry = expiry;
      }
      
      @Override
      public void run() {
         try {
            while(true) {
               long currentTime = System.currentTimeMillis();
               long expireTime = expiry.get();
               
               if(expireTime > 0 && currentTime >= expireTime) {
                  expired.set(true);
                  break;
               }
               Thread.sleep(1000);
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            TerminateHandler.terminate("Time limit expired");
         }
      }
   }

}
