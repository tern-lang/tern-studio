package tern.studio.service.message;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import tern.common.thread.ThreadPool;
import tern.studio.agent.event.MessageEnvelope;
import tern.studio.agent.event.ProcessEvent;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProcessEventListener;
import tern.studio.agent.event.ProcessEventMarshaller;
import tern.studio.agent.event.ProcessEventRouter;
import tern.studio.agent.event.ProcessEventType;

public class AsyncEventExchanger implements MessageEnvelopeProcessor {

   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final Map<String, ProcessEventChannel> channels;
   private final ProcessEventRouter router;
   private final Executor executor;
   private final Reactor reactor;
   
   public AsyncEventExchanger(ProcessEventListener listener) throws IOException {
      this(listener, 5);
   }

   public AsyncEventExchanger(ProcessEventListener listener, int threads) throws IOException {
      this.marshallers = new ConcurrentHashMap<Integer, ProcessEventMarshaller>();
      this.channels = new ConcurrentHashMap<String, ProcessEventChannel>();
      this.router = new ProcessEventRouter(listener);
      this.executor = new ThreadPool(threads);
      this.reactor = new ExecutorReactor(executor);
   }
   
   public void connect(Channel channel, String process) throws Exception {
      MessageEnvelopeCollector collector = new MessageEnvelopeCollector(this, reactor, executor, channel, process);
      reactor.process(collector);
   }
   
   @Override
   public void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception {
      if(marshallers.isEmpty()) {
         ProcessEventType[] events = ProcessEventType.values();
         
         for(ProcessEventType event : events) {
            ProcessEventMarshaller marshaller = event.marshaller.newInstance();
            marshallers.put(event.code, marshaller);
         }
      }
      int code = message.getCode();
      ProcessEventMarshaller marshaller = marshallers.get(code);

      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + code);
      }
      ProcessEvent event = marshaller.fromMessage(message);
      String process = event.getProcess();
      
      channels.put(process, channel);
      router.route(channel, event);
   }
}