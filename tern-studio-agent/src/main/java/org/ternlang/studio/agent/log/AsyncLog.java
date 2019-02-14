package org.ternlang.studio.agent.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.common.thread.ThreadBuilder;

public class AsyncLog implements Log {
   
   private static final String TIME_FORMAT = "HH:mm:ss";
   private static final int EVENT_LIMIT = 10000;
   
   private final LogDispatcher dispatcher;
   private final DateFormatter formatter;
   private final LogLevel enabled;
   private final Log logger;
   
   public AsyncLog(Log logger, LogLevel enabled) {
      this.dispatcher = new LogDispatcher(EVENT_LIMIT);
      this.formatter = new DateFormatter(TIME_FORMAT);
      this.enabled = enabled;
      this.logger = logger;
   }
   
   public void log(LogLevel level, Object message) {
      if(enabled.isLevelEnabled(level)) {
         LogEvent event = new LogEvent(level, message, null);
         dispatcher.log(event);
      }
   }
   
   public void log(LogLevel level, Object message, Throwable cause) {
      if(enabled.isLevelEnabled(level)) {
         LogEvent event = new LogEvent(level, message, cause);
         dispatcher.log(event);
      }
   }
   
   public void stop() {
      dispatcher.stop();
   }
   
   private class DateFormatter extends ThreadLocal<DateFormat> {
      
      private final String format;
      
      public DateFormatter(String format) {
         this.format = format;
      }
      
      @Override
      public DateFormat initialValue(){
         return new SimpleDateFormat(format);
      }
   }
   
   private class LogDispatcher implements Runnable {
      
      private final BlockingQueue<LogEvent> queue;
      private final ThreadFactory factory;
      private final AtomicBoolean active;
      private final AtomicBoolean stopped;

      public LogDispatcher(int capacity) {
         this.factory = new ThreadBuilder();
         this.queue = new ArrayBlockingQueue<LogEvent>(capacity);
         this.active = new AtomicBoolean();
         this.stopped = new AtomicBoolean();
      }
      
      public void log(LogEvent event) {
         try {
            if(active.compareAndSet(false, true)) {
               Thread thread = factory.newThread(this);
               thread.start();
            }
            queue.offer(event, 10000, TimeUnit.MILLISECONDS);
         }catch(Exception e) {
            throw new IllegalStateException("Could not log event", e);
         }
      }
      
      @Override
      public void run() {
         try {
            while(!stopped.get()) {
               try {
                  LogEvent event = queue.poll(1000, TimeUnit.MILLISECONDS);
                  
                  if(event != null) {
                     event.run();
                  }
               } catch(Exception e) {
                  throw new IllegalStateException("Could not poll queue", e);
               }
            }
         } finally {
            stopped.set(true);
         }
      }
      
      public void stop(){
         stopped.set(true); // can never start again
      }
   }
   
   private class LogEvent implements Runnable {
      
      private final LogLevel level;
      private final Throwable cause;
      private final Object message;
      private final Thread thread;
      private final long time;
      
      public LogEvent(LogLevel level, Object message, Throwable cause) {
         this.time = System.currentTimeMillis();
         this.thread = Thread.currentThread();
         this.message = message;
         this.cause = cause;
         this.level = level;
      }
      
      @Override
      public void run() {
         String name = thread.getName();
         DateFormat format = formatter.get();
         String date = format.format(time);
         
         if(message != null) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(date);
            builder.append(" [");
            builder.append(name);
            builder.append("] ");
            builder.append(message);
            
            if(cause != null) {
               logger.log(level, builder, cause);
            } else {
               logger.log(level, builder);
            }
         }
      }
   }
}
