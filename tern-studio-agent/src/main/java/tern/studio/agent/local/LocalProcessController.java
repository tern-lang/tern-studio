package tern.studio.agent.local;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import tern.common.thread.ThreadBuilder;
import tern.core.module.Path;
import tern.studio.agent.ProcessAgent;
import tern.studio.agent.ProcessClient;
import tern.studio.agent.ProcessContext;
import tern.studio.agent.local.message.AttachRequest;
import tern.studio.agent.local.message.DetachRequest;
import tern.studio.agent.log.LogLevel;

public class LocalProcessController {

   private final AtomicReference<ProcessClient> reference;
   private final ConnectLauncher launcher;
   private final ProcessContext context;
   private final AtomicBoolean active;
   private final CountDownLatch latch;
   private final Path script;
   
   public LocalProcessController(ProcessContext context, CountDownLatch latch, Path script, int port) {
      this.launcher = new ConnectLauncher(context, this, port);
      this.reference = new AtomicReference<ProcessClient>();
      this.active = new AtomicBoolean();
      this.context = context;
      this.script = script;
      this.latch = latch;
   }

   public String attachRequest(AttachRequest request) {
      String project = request.getProject();
      URI root = request.getTarget();
      String path = script.getPath();
      
      if(!isAttached()) {
         try {
            ProcessAgent agent = new ProcessAgent(context, LogLevel.INFO);
            ProcessClient client = agent.start(root, launcher);
            
            System.err.println("Debug agent attached to " + root);
            reference.set(client);
            client.attachProcess(project, path);
            latch.countDown();
         }catch(Exception e){
            e.printStackTrace();
         }
      }
      return context.getProcess();
   }
   
   public String detachRequest(DetachRequest request) {
      ProcessClient client = reference.getAndSet(null);
      
      try {
         if(client != null) {
            System.err.println("Debug agent detached");
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
      return context.getProcess(); 
   }
   
   public void reset() {
      ProcessClient client = reference.getAndSet(null);
      
      try {
         if(client != null) {
            System.err.println("Debug agent detached");
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public boolean isAttached(){
      return reference.get() != null;
   }
   
   public void start() {
      launcher.run();
   }
   
   private class ConnectLauncher implements Runnable {
      
      private final LocalProcessController listener;
      private final ConnectAcceptor acceptor;
      private final ThreadFactory factory;
      
      public ConnectLauncher(ProcessContext context, LocalProcessController listener, int port) {
         this.acceptor = new ConnectAcceptor(listener, port);
         this.factory = new ThreadBuilder();
         this.listener = listener;
      }

      @Override
      public void run() {
         listener.reset();
         
         if(active.compareAndSet(false, true)) {
            Thread thread = factory.newThread(acceptor);
            thread.start();
         }
      }      
   }
   
   private class ConnectAcceptor implements Runnable {
      
      private final LocalMessageConsumer consumer;
      private final int port;
      
      public ConnectAcceptor(LocalProcessController listener, int port) {
         this.consumer = new LocalMessageConsumer(listener);
         this.port = port;
      }
      
      public void run() {
         try {
            ServerSocket listener = new ServerSocket(port);
            
            try {
               int local = listener.getLocalPort();
               
               System.err.println("Debug agent listening on " + local);
               
               while(active.get()) {
                  Socket socket = listener.accept();
      
                  try {
                     consumer.consume(socket);
                  }catch(Exception e) {
                     e.printStackTrace();
                  }finally {
                     socket.close();
                  }
               }
            } finally {
               listener.close();
            }
         } catch(Exception e){
            e.printStackTrace();
         } finally {
            active.set(false);
         }
         
      }
   }
}
