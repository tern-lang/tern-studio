package tern.studio.agent.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import tern.common.thread.ThreadBuilder;

public class QueueExecutor implements Executor {

   private final BlockingQueue<Runnable> tasks;
   private final TaskExecutor executor;
   private final ThreadBuilder builder;
   private final AtomicBoolean active;

   public QueueExecutor() {
      this.tasks = new LinkedBlockingQueue<Runnable>();
      this.executor = new TaskExecutor(1000);
      this.builder = new ThreadBuilder();
      this.active = new AtomicBoolean();
   }

   @Override
   public void execute(Runnable runnable) {
      if(!active.get()) {
         throw new IllegalStateException("Executor is not running");
      }
      tasks.offer(runnable);
   }

   public void start() {
      if (active.compareAndSet(false, true)) {
         Thread thread = builder.newThread(executor);
         thread.start();
      }
   }

   public void stop() {
      if (active.compareAndSet(true, false)) {
         tasks.clear();
      }
   }

   private class TaskExecutor implements Runnable {

      private final long wait;

      public TaskExecutor(long wait) {
         this.wait = wait;
      }

      @Override
      public void run() {
         try {
            while (active.get()) {
               Runnable task = tasks.poll(wait, TimeUnit.MILLISECONDS);

               if (task != null) {
                  try {
                     task.run();
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            active.set(false);
         }
      }
   }
}