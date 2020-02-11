package org.ternlang.studio.core.message;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.agent.message.event.ProcessOrigin;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.message.ByteArrayFrame;
import org.ternlang.studio.agent.event.MessageEnvelope;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ProcessEventListener;
import org.ternlang.studio.agent.event.ProcessEventRouter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class AsyncEventExchanger implements MessageEnvelopeProcessor {

   private final Map<String, ProcessEventChannel> channels;
   private final ProcessEventRouter router;
   private final Executor executor;
   private final Reactor reactor;
   
   public AsyncEventExchanger(ProcessEventListener listener) throws IOException {
      this(listener, 5);
   }

   public AsyncEventExchanger(ProcessEventListener listener, int threads) throws IOException {
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
      byte[] data = message.getData();
      int offset = message.getOffset();
      int length = message.getLength();

      if(length > 0) {
         ByteArrayFrame frame = new ByteArrayFrame();
         ProcessEventCodec codec = new ProcessEventCodec();
         
         frame.wrap(data, offset, length);
         codec.with(frame, 0, length);
   
         ProcessOrigin origin = codec.get();
         String process = origin.process().toString();
         
         channels.put(process, channel);
         router.route(channel, origin);
      }
   }
}