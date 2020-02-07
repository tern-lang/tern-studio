package org.ternlang.studio.agent.client;

import static java.lang.Short.MAX_VALUE;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.agent.message.event.ProcessEventBuilder;
import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.message.ByteArrayFrame;
import org.ternlang.studio.agent.core.QueueExecutor;
import org.ternlang.studio.agent.event.MessageEnvelope;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ProcessEventConnection;
import org.ternlang.studio.agent.event.ProcessEventConsumer;
import org.ternlang.studio.agent.event.ProcessEventListener;
import org.ternlang.studio.agent.event.ProcessEventProducer;
import org.ternlang.studio.agent.log.TraceLogger;

public class ConnectTunnelClient {
   
   private static final String THREAD_NAME = "%s: %s@%s:%s";
   
   private final ProcessEventListener listener;
   private final QueueExecutor executor;
   private final ConnectionChecker checker;
   private final TraceLogger logger;
   
   public ConnectTunnelClient(ProcessEventListener listener, ConnectionChecker checker, TraceLogger logger) throws IOException {
      this.executor = new QueueExecutor();
      this.listener = listener;
      this.checker = checker;
      this.logger = logger;
   }
   
   public ProcessEventChannel connect(String process, String host, int port) throws Exception {
      String type = SocketConnection.class.getSimpleName();
      String name = String.format(THREAD_NAME, type, process, host, port);
      
      try {
         Socket socket = new Socket(host, port);
         ConnectTunnelHandler tunnel = new ConnectTunnelHandler(logger, process, port);
         InputStream input = socket.getInputStream();
         OutputStream output = socket.getOutputStream();
         SocketClientSession session = new SocketClientSession(checker, socket);
         SocketConnection connection = new SocketConnection(session, input, output);
      
         executor.start();
         connection.setName(name);
         tunnel.tunnel(socket); // do the tunnel handshake
         socket.setSoTimeout(10000);
         connection.start();
         return connection;
      }catch(Exception e) {
         throw new IllegalStateException("Could not connect to " + host + ":" + port, e);
      }
   }
   
   private class SocketClientSession implements Closeable {
      
      private final ConnectionChecker checker;
      private final Socket socket;
      
      public SocketClientSession(ConnectionChecker checker, Socket socket) {
         this.checker = checker;
         this.socket = socket;
      }

      @Override
      public void close() {
         try {
            checker.close();
         } catch(Exception e) {
            e.printStackTrace();
         }
         try {
            socket.shutdownInput();
         } catch(Exception e) {
            //e.printStackTrace();
         }
         try {
            socket.shutdownOutput();
         } catch(Exception e) {
            //e.printStackTrace();
         }
         try {
            executor.stop();
            socket.close();
         } catch(Exception e) {
            //e.printStackTrace();
         }
      }
   }

   private class SocketConnection extends Thread implements ProcessEventChannel {
      
      private final ProcessEventConnection connection;
      private final SocketClientSession session;
      private final ConnectEventHandler handler;
      private final ProcessEventCodec codec;
      private final ByteArrayFrame frame;
      private final AtomicBoolean closed;
      private final Set<Class> events;
      
      public SocketConnection(SocketClientSession session, InputStream input, OutputStream output) throws IOException {
         this.connection = new ProcessEventConnection(logger, executor, input, output, session);
         this.handler = new ConnectEventHandler(this, listener);
         this.events = new CopyOnWriteArraySet<Class>();
         this.frame = new ByteArrayFrame();
         this.codec = new ProcessEventCodec();
         this.closed = new AtomicBoolean();
         this.session = session;
      }

      @Override
      public ProcessEventBuilder begin() {
         frame.clear();
         codec.with(frame, 0, MAX_VALUE);
         return codec;
      }
      
      @Override
      public boolean send() throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = "process-xx";

         try {
            int length = frame.length();
            byte[] array = frame.getByteArray();
            MessageEnvelope envelope = new MessageEnvelope(0, array, 0, length);

            producer.produce(envelope);
            return true;
         } catch(Exception e) {
            logger.info(process + ": Error sending event", e);
            close(process + ": Error sending event: " + e);
         }
         return false;
      }

      @Override
      public boolean sendAsync() throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = "process-xx";

         try {
            int length = frame.length();
            byte[] array = frame.getByteArray();
            byte[] copy = Arrays.copyOf(array, length); // copy if async
            MessageEnvelope envelope = new MessageEnvelope(0, copy, 0, length);

            Future<Boolean> future = producer.produceAsync(envelope);
            return future.get();
         } catch(Exception e) {
            logger.info(process + ": Error sending async event", e);
            close(process + ": Error sending async event: " + e);
         }
         return false;
      }
      
      @Override
      public void run() {
         try {
            ProcessEventConsumer consumer = connection.getConsumer();
            
            while(true) {
               consumer.consume(handler);
            }
         } catch(Exception e) {
            logger.info("Error processing events ["+ events + "]", e);
            close("Error in event loop: " + e);
         } finally {
            close("Event loop has finished");
         }
      }
      
      @Override
      public void close(String reason) {
         try {
            ProcessEventProducer producer = connection.getProducer();
            
            if(closed.compareAndSet(false, true)) {
               listener.onClose(this);
               producer.close(reason);
            }
            session.close();
         } catch(Exception e) {
            logger.info("Error closing client connection", e);
         }
      }
   }
}