package tern.studio.service.message;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

@Slf4j
public class MessageEnvelopeCollector implements Operation {
   
   private final MessageEnvelopeConsumer consumer;
   private final Reactor reactor;
   private final Channel channel;
   
   public MessageEnvelopeCollector(AsyncEventExchanger router, Reactor reactor, Executor executor, Channel channel, String process) {
      this.consumer = new MessageEnvelopeConsumer(router, executor, channel, process);
      this.reactor = reactor;
      this.channel = channel;
   }

   @Override
   public Trace getTrace() {
      return channel.getTrace();
   }

   @Override
   public SelectableChannel getChannel() {
      return channel.getSocket();
   }

   @Override
   public void run() {
      try {
         ByteCursor cursor = channel.getCursor();
         
         while(cursor.isReady()) {
            consumer.consume(cursor);
         }
         if(cursor.isOpen()) {
            reactor.process(this, SelectionKey.OP_READ);
         }
      }catch(Exception e) {
         e.printStackTrace();
         log.debug("Could not consume message", e);
         cancel(); // close the transport
      }
   }

   @Override
   public void cancel() {
      try {
         channel.close();
      }catch(Exception e) {
         log.debug("Could not close transport", e);
      }
   }
}