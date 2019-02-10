package tern.studio.agent.event;

import java.io.Closeable;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import tern.studio.agent.log.TraceLogger;

public class ProcessEventProducer {
   
   private final Map<Class, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeWriter writer;
   private final TraceLogger logger;
   private final Closeable closeable;
   private final Executor executor;
   
   public ProcessEventProducer(TraceLogger logger, OutputStream stream, Closeable closeable, Executor executor) {
      this.marshallers = new ConcurrentHashMap<Class, ProcessEventMarshaller>();
      this.writer = new MessageEnvelopeWriter(stream, closeable);
      this.closeable = closeable;
      this.executor = executor;
      this.logger = logger;
   }
   
   public void produce(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
      //executor.execute(task);
      task.call();
   }

   public Future<Boolean> produceAsync(ProcessEvent event) throws Exception {
      SendTask task = new SendTask(event);
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
      
      private final ProcessEvent event;
      
      public SendTask(ProcessEvent event) {
         this.event = event;
      }
      
      @Override
      public Boolean call() throws Exception {
         Class type = event.getClass();
         
         try {
            if (!marshallers.containsKey(type)) {
               ProcessEventType[] events = ProcessEventType.values();
      
               for (ProcessEventType event : events) {
                  ProcessEventMarshaller marshaller = event.marshaller.newInstance();
                  marshallers.put(event.event, marshaller);
               }
            }
            ProcessEventMarshaller marshaller = marshallers.get(type);
            MessageEnvelope message = marshaller.toMessage(event);
      
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