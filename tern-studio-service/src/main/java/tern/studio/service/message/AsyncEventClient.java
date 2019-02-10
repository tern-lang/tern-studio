package tern.studio.service.message;

import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.transport.Channel;
import org.slf4j.Logger;
import tern.studio.agent.event.ProcessEvent;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProcessEventProducer;
import tern.studio.agent.log.Log;
import tern.studio.agent.log.LogLevel;
import tern.studio.agent.log.LogLogger;
import tern.studio.agent.log.TraceLogger;

@Slf4j
public class AsyncEventClient implements ProcessEventChannel {

   private final ProcessEventProducer producer;
   private final TraceLogger adapter;
   private final OutputStream stream;
   private final AtomicBoolean open;
   private final Log logger;
   
   public AsyncEventClient(Executor executor, Channel channel) {
      this.logger = new LoggerLog(log);
      this.adapter = new LogLogger(logger, LogLevel.DEBUG);
      this.stream = new ChannelOutputStream(channel);
      this.producer = new ProcessEventProducer(adapter, stream, stream, executor);
      this.open = new AtomicBoolean(true);
   }
   
   @Override
   public boolean send(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         producer.produce(event);
         return true;
      } catch(Exception e) {
         adapter.info(process + ": Error sending event", e);
         close(process + ": Error sending event " + event + ": " +e);
      }
      return false;
   }

   @Override
   public boolean sendAsync(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      
      try {
         Future<Boolean> future = producer.produceAsync(event);
         return future.get();
      } catch(Exception e) {
         adapter.info(process + ": Error sending event", e);
         close(process + ": Error sending async event " + event + ": " +e);
      }
      return false;
   }

   @Override
   public void close(String reason) throws Exception {
      try {
         if(open.compareAndSet(true, false)) {
            producer.close(reason);
         }
      } catch(Exception e) {
         adapter.info("Error closing socket", e);
      } 
   }
   
   private static class LoggerLog implements Log {
      
      private final Logger log;
      
      public LoggerLog(Logger log) {
         this.log = log;
      }

      @Override
      public void log(LogLevel level, Object text) {
         String message = String.valueOf(text);
         
         if(level == LogLevel.TRACE) {
            log.trace(message);
         } else if(level == LogLevel.DEBUG) {
            log.debug(message);
         } else if(level == LogLevel.INFO) {
            log.info(message);
         } else {
            log.error(message);
         }
      }

      @Override
      public void log(LogLevel level, Object text, Throwable cause) {
         String message = String.valueOf(text);
         
         if(level == LogLevel.TRACE) {
            log.trace(message, cause);
         } else if(level == LogLevel.DEBUG) {
            log.debug(message);
         } else if(level == LogLevel.INFO) {
            log.info(message);
         } else {
            log.error(message);
         }
      }
      
   }
}