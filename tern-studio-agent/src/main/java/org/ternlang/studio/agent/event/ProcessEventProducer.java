package org.ternlang.studio.agent.event;

import java.io.Closeable;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.ternlang.studio.agent.log.TraceLogger;

public class ProcessEventProducer {

   private final MessageEnvelopeWriter writer;
   private final TraceLogger logger;
   private final Closeable closeable;
   private final Executor executor;
   
   public ProcessEventProducer(TraceLogger logger, OutputStream stream, Closeable closeable, Executor executor) {
      this.writer = new MessageEnvelopeWriter(stream, closeable);
      this.closeable = closeable;
      this.executor = executor;
      this.logger = logger;
   }
   
   public void produce(MessageEnvelope message) throws Exception {
      SendTask task = new SendTask(message);
      //executor.execute(task);
      task.call();
   }

   public Future<Boolean> produceAsync(MessageEnvelope message) throws Exception {
      SendTask task = new SendTask(message);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      return future;
   }
   
   public void close(String reason) throws Exception {
      CloseTask task = new CloseTask(reason);
      FutureTask<Boolean> future = new FutureTask<Boolean>(task);

      executor.execute(future);
      future.get();
   }
   
   private class CloseTask implements Callable<Boolean> {
      
      private final Exception cause;
      private final String reason;
      
      public CloseTask(String reason) {
         this.cause = new Exception("Closing connection: " + reason);
         this.reason = reason;
      }
      
      @Override
      public Boolean call() throws Exception {
         try {
            logger.info("Closing connection: " + reason);
            //cause.printStackTrace();
            closeable.close();
            writer.close();
         }catch(Exception e) {
            throw new IllegalStateException("Could not close writer: " + reason);
         }
         return true;
      }
   }
   
   private class SendTask implements Callable<Boolean> {
      
      private final MessageEnvelope message;
      
      public SendTask(MessageEnvelope message) {
         this.message = message;
      }
      
      @Override
      public Boolean call() throws Exception {
         try {
            writer.write(message);
            return true;
         }catch(Exception e){
            logger.info("Error sending event", e);
            closeable.close();
            throw new IllegalStateException("Error writing message", e);
         }
      }
   }
}