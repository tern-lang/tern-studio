package org.ternlang.studio.agent.client;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.ternlang.agent.message.common.ExecuteData;
import org.ternlang.agent.message.event.PingEvent;
import org.ternlang.agent.message.event.PongEventBuilder;
import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.core.ExecuteState;
import org.ternlang.studio.agent.core.ExecuteStatus;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.event.ProcessEventChannel;

public class ConnectionChecker implements Closeable {

   private final Set<ConnectionListener> listeners;
   private final ProcessContext context;
   private final ThreadFactory factory;
   private final HealthChecker checker;
   private final AtomicBoolean active;
   private final AtomicLong update;
   private final String process;
   
   public ConnectionChecker(ProcessContext context, String process) {
      this.listeners = new CopyOnWriteArraySet<ConnectionListener>();
      this.active = new AtomicBoolean();
      this.update = new AtomicLong();
      this.checker = new HealthChecker(this, active, update, 10000);
      this.factory = new ThreadBuilder();
      this.context = context;
      this.process = process;
   }
   
   public void register(ConnectionListener listener) {
      listeners.add(listener);
   }
   
   public void remove(ConnectionListener listener) {
      listeners.remove(listener);
   }
   
   public void update(ProcessEventChannel channel, PingEvent event) {
      ProcessMode mode = context.getMode();
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      ExecuteStatus status = state.getStatus();
      ExecuteData data = state.getData();
      CharSequence project = data.project();
      CharSequence resource = data.resource();
      String system = state.getSystem();
      String pid = state.getPid();
      long time = System.currentTimeMillis();
      
      try {
         long remainingTime = Math.max(0, context.getTimeLimiter().getTimeLimit().getExpiryTime() - System.currentTimeMillis());
         long timeout = context.getTimeLimiter().getTimeLimit().getTimeout();
         
         PongEventBuilder pong = channel.begin()
            .pong()
            .pid(pid)
            .system(system)
            .project(project)
            .status(org.ternlang.agent.message.common.ExecuteStatus.resolve(status.name()))
            .usedTime(timeout - remainingTime)
            .totalTime(timeout)
            .usedMemory(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
            .totalMemory(Runtime.getRuntime().totalMemory())
            .threads(Thread.getAllStackTraces().size()); // this might be expensive

         pong.resource().some(resource);
         
         if(mode.isDetachRequired()) {
            if(!status.isFinished()) { // send pong only if still running
               if(!channel.send()) {
                  if(mode.isTerminateRequired()) {
                     TerminateHandler.terminate("Ping failed for " + process);
                  }
               } else {
                  update.set(time);
               }
            }
         } else {
            if(!channel.send()) {
               if(mode.isTerminateRequired()) {
                  TerminateHandler.terminate("Ping failed for " + process);
               }
            } else {
               update.set(time);
            }
         }
      } catch(Exception e) {
         e.printStackTrace();
         
         if(mode.isTerminateRequired()) {
            TerminateHandler.terminate("Ping failed for " + process + " with " + e);
         }
      }
   }
   
   public void start() {
      if(active.compareAndSet(false, true)) {
         Thread thread = factory.newThread(checker);
         String type = HealthChecker.class.getSimpleName();
         String name = thread.getName();
         
         thread.setName(type + ": " +name);
         thread.start();
      }
   }
   
   @Override
   public void close() {
      if(active.compareAndSet(true, false)) {
         for(ConnectionListener listener : listeners) {
            try {
               listener.onClose();
            } catch(Exception e) {
               e.printStackTrace();
            }finally {
               listeners.remove(listener);
            }
         }
      }
   }
   
   private static class HealthChecker implements Runnable {
      
      private final ConnectionChecker checker;
      private final AtomicBoolean active;
      private final AtomicLong update;
      private final long frequency;
      
      public HealthChecker(ConnectionChecker checker, AtomicBoolean active, AtomicLong update, long frequency) {
         this.frequency = frequency;
         this.checker = checker;
         this.active = active;
         this.update = update;
      }
      
      @Override
      public void run() {
         try {
            while(active.get()) {
               Thread.sleep(frequency);
               long last = update.get();
               long time = System.currentTimeMillis();
               long duration = time - last;
               
               if(duration > frequency) { // ensure pings are frequent
                  break;
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            checker.close();
         }
      }
   }

}