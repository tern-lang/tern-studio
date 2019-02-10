package tern.studio.agent.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import tern.studio.agent.core.QueueExecutor;
import tern.studio.agent.event.BeginEvent;
import tern.studio.agent.event.BreakpointsEvent;
import tern.studio.agent.event.BrowseEvent;
import tern.studio.agent.event.EvaluateEvent;
import tern.studio.agent.event.ExecuteEvent;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.FaultEvent;
import tern.studio.agent.event.PingEvent;
import tern.studio.agent.event.PongEvent;
import tern.studio.agent.event.ProcessEvent;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProcessEventConnection;
import tern.studio.agent.event.ProcessEventConsumer;
import tern.studio.agent.event.ProcessEventListener;
import tern.studio.agent.event.ProcessEventProducer;
import tern.studio.agent.event.ProfileEvent;
import tern.studio.agent.event.RegisterEvent;
import tern.studio.agent.event.ScopeEvent;
import tern.studio.agent.event.ScriptErrorEvent;
import tern.studio.agent.event.StepEvent;
import tern.studio.agent.event.WriteErrorEvent;
import tern.studio.agent.event.WriteOutputEvent;
import tern.studio.agent.log.TraceLogger;

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
      private final AtomicBoolean closed;
      private final Set<Class> events;
      
      public SocketConnection(SocketClientSession session, InputStream input, OutputStream output) throws IOException {
         this.connection = new ProcessEventConnection(logger, executor, input, output, session);
         this.events = new CopyOnWriteArraySet<Class>();
         this.closed = new AtomicBoolean();
         this.session = session;
      }
      
      @Override
      public boolean send(ProcessEvent event) throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = event.getProcess();
         
         try {
            producer.produce(event);
            return true;
         } catch(Exception e) {
            logger.info(process + ": Error sending event", e);
            close(process + ": Error sending event " +event + ": " + e);
         }
         return false;
      }

      @Override
      public boolean sendAsync(ProcessEvent event) throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = event.getProcess();

         try {
            Future<Boolean> future = producer.produceAsync(event);
            return future.get();
         } catch(Exception e) {
            logger.info(process + ": Error sending async event", e);
            close(process + ": Error sending async event " +event + ": " + e);
         }
         return false;
      }
      
      @Override
      public void run() {
         try {
            ProcessEventConsumer consumer = connection.getConsumer();
            
            while(true) {
               ProcessEvent event = consumer.consume();
               Class type = event.getClass();
               
               events.add(type);
               
               if(event instanceof ExitEvent) {
                  listener.onExit(this, (ExitEvent)event);
               } else if(event instanceof ExecuteEvent) {
                  listener.onExecute(this, (ExecuteEvent)event);                  
               } else if(event instanceof RegisterEvent) {
                  listener.onRegister(this, (RegisterEvent)event);
               } else if(event instanceof ScriptErrorEvent) {
                  listener.onScriptError(this, (ScriptErrorEvent)event);
               } else if(event instanceof WriteErrorEvent) {
                  listener.onWriteError(this, (WriteErrorEvent)event);
               } else if(event instanceof WriteOutputEvent) {
                  listener.onWriteOutput(this, (WriteOutputEvent)event);
               } else if(event instanceof PingEvent) {
                  listener.onPing(this, (PingEvent)event);
               } else if(event instanceof PongEvent) {
                  listener.onPong(this, (PongEvent)event);
               } else if(event instanceof ScopeEvent) {
                  listener.onScope(this, (ScopeEvent)event);
               } else if(event instanceof BreakpointsEvent) {
                  listener.onBreakpoints(this, (BreakpointsEvent)event);
               } else if(event instanceof BeginEvent) {
                  listener.onBegin(this, (BeginEvent)event);
               } else if(event instanceof StepEvent) {
                  listener.onStep(this, (StepEvent)event);
               } else if(event instanceof BrowseEvent) {
                  listener.onBrowse(this, (BrowseEvent)event);
               } else if(event instanceof EvaluateEvent) {
                  listener.onEvaluate(this, (EvaluateEvent)event);                  
               } else if(event instanceof ProfileEvent) {
                  listener.onProfile(this, (ProfileEvent)event);
               } else if(event instanceof FaultEvent) {
                  listener.onFault(this, (FaultEvent)event);
               }
            }
         }catch(Exception e) {
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