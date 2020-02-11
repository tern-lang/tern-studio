package org.ternlang.studio.core.message;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.transport.Channel;
import org.slf4j.Logger;
import org.ternlang.agent.message.event.ProcessEventBuilder;
import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.message.ByteArrayFrame;
import org.ternlang.studio.agent.event.MessageEnvelope;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ProcessEventProducer;
import org.ternlang.studio.agent.log.Log;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.log.LogLogger;
import org.ternlang.studio.agent.log.TraceLogger;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Short.MAX_VALUE;

@Slf4j
public class AsyncEventClient implements ProcessEventChannel {

   private final ProcessEventProducer producer;
   private final ProcessEventCodec codec;
   private final ByteArrayFrame frame;
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
      this.frame = new ByteArrayFrame();
      this.codec = new ProcessEventCodec();
   }

   @Override
   public ProcessEventBuilder begin() {
      frame.clear();
      codec.with(frame, 0, MAX_VALUE);
      return codec;
   }
   
   @Override
   public boolean send() throws Exception {
      String process = "process-xx";

      try {
         int length = frame.length();
         byte[] array = frame.getByteArray();
         MessageEnvelope envelope = new MessageEnvelope(0, array, 0, length);

         producer.produce(envelope);
         return true;
      } catch(Exception e) {
         adapter.info(process + ": Error sending event", e);
         close(process + ": Error sending event: " +e);
      }
      return false;
   }

   @Override
   public boolean sendAsync() throws Exception {
      String process = "process-xx";
      
      try {
         int length = frame.length();
         byte[] array = frame.getByteArray();
         byte[] copy = Arrays.copyOf(array, length); // copy if async
         MessageEnvelope envelope = new MessageEnvelope(0, copy, 0, length);

         Future<Boolean> future = producer.produceAsync(envelope);
         return future.get();
      } catch(Exception e) {
         adapter.info(process + ": Error sending event", e);
         close(process + ": Error sending async event: " +e);
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