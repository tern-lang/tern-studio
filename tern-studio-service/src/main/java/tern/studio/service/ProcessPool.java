package tern.studio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.transport.Channel;
import tern.common.Cache;
import tern.common.LeastRecentlyUsedCache;
import tern.common.thread.ThreadBuilder;
import tern.studio.agent.event.BeginEvent;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.FaultEvent;
import tern.studio.agent.event.PongEvent;
import tern.studio.agent.event.ProcessEventAdapter;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProcessEventListener;
import tern.studio.agent.event.ProfileEvent;
import tern.studio.agent.event.RegisterEvent;
import tern.studio.agent.event.ScopeEvent;
import tern.studio.agent.event.ScriptErrorEvent;
import tern.studio.agent.event.WriteErrorEvent;
import tern.studio.agent.event.WriteOutputEvent;
import tern.studio.common.console.ConsoleManager;
import tern.studio.project.Workspace;
import tern.studio.project.config.ProcessConfiguration;
import tern.studio.service.message.AsyncEventExchanger;

@Slf4j
public class ProcessPool {
   
   private static final long DEFAULT_WAIT_TIME = 10000;
   private static final long DEFAULT_PING_FREQUENCY = 4000;
   
   private final Cache<String, Long> active;
   private final BlockingQueue<ProcessConnection> running;
   private final Set<ProcessEventListener> listeners;
   private final ProcessConfiguration configuration;
   private final ProcessEventInterceptor interceptor;
   private final ProcessConnectionPool waiting;
   private final ProcessAgentStarter starter;
   private final AsyncEventExchanger router;
   private final ProcessLauncher launcher;
   private final ProcessAgentPinger pinger;
   private final ProcessNameFilter filter;
   private final ConsoleManager manager;
   private final ProcessListener listener;
   private final ThreadFactory factory;
   private final Workspace workspace;
   private final int capacity;
   
   public ProcessPool(ProcessConfiguration configuration, ProcessLauncher launcher, ProcessNameFilter filter, Workspace workspace, int capacity) throws IOException {
      this(configuration, launcher, filter, workspace, capacity, DEFAULT_PING_FREQUENCY);
   }
   
   public ProcessPool(ProcessConfiguration configuration, ProcessLauncher launcher, ProcessNameFilter filter, Workspace workspace, int capacity, long frequency) throws IOException {
      this.waiting = new ProcessConnectionPool();
      this.active = new LeastRecentlyUsedCache<String, Long>();
      this.listeners = new CopyOnWriteArraySet<ProcessEventListener>();
      this.running = new LinkedBlockingQueue<ProcessConnection>();
      this.interceptor = new ProcessEventInterceptor(listeners);
      this.router = new AsyncEventExchanger(interceptor);
      this.pinger = new ProcessAgentPinger(frequency);
      this.starter = new ProcessAgentStarter(pinger);
      this.listener = new ProcessListener();
      this.manager = new ConsoleManager(listener, frequency);
      this.factory = new ThreadBuilder();
      this.configuration = configuration;
      this.workspace = workspace;
      this.launcher = launcher;
      this.capacity = capacity;
      this.filter = filter;
   }

   public ProcessConnection acquire(String process) {
      try {
         ProcessConnection connection = waiting.acquire(DEFAULT_WAIT_TIME, process); // take a process from the pool
         
         if(connection == null) {
            if(process == null) {
               throw new IllegalStateException("No agent found as pool is empty");
            }
            throw new IllegalStateException("The agent '" + process + "' is not available in pool");
         }
         long time = System.currentTimeMillis();
         String agent = connection.getProcess();
         
         active.cache(agent, time);
         running.offer(connection);
         launch(); // start a process straight away
         return connection;
      }catch(Exception e){
         log.info("Could not acquire process", e);
      }
      return null;
   }
   
   public void register(ProcessEventListener listener) {
      try {
         listeners.add(listener);
      }catch(Exception e){
         log.info("Could not register process listener", e);
      }
   }
   
   public void remove(ProcessEventListener listener) {
      try {
         listeners.remove(listener);
      }catch(Exception e){
         log.info("Could not remove process listener", e);
      }
   }
   
   public boolean ping(String process, long time) {
      try {
         ProcessConnection connection = waiting.acquire(0, process); // take a process from the pool
         
         if(connection != null) {
            if(connection.ping(time)) {
               waiting.register(connection); // add back if ping succeeded
               return true;
            }
            connection.close(process + " Ping did not succeed");
         }
      }catch(Exception e) {
         log.info("Could not ping '" + process + "'", e);
      }
      return false;
   }
   
   public void connect(Channel channel, String process) {
      try {
         router.connect(channel, process);
      } catch(Exception e) {
         log.info("Could not connect channel", e);
      }
   }
   
   public void start(String host, int port) { // http://host:port/project
      try {
         manager.start();
         pinger.start(host, port);
      } catch(Exception e) {
         log.info("Could not start pool on port " + port, e);
      }
   }
   
   public void launch() { // launch a new process!!
      try {
         if(capacity > 0){
            Thread thread = factory.newThread(starter);
            thread.start();
         }
      } catch(Exception e) {
         log.info("Could not launch process", e);
      }
   }
   
   public boolean active(String process) {
      return active.contains(process);
   }
   
   private class ProcessEventInterceptor extends ProcessEventAdapter {
      
      private final Set<ProcessEventListener> listeners;
      
      public ProcessEventInterceptor(Set<ProcessEventListener> listeners) {
         this.listeners = listeners;
      }
      
      @Override
      public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {
         String process = event.getProcess();
         ProcessConnection connection = new ProcessConnection(channel, workspace, process);
         
         log.info("Registered new connection " + process);
         waiting.register(connection);
         pinger.ping(); // force ping quickly

         for(ProcessEventListener listener : listeners) {
            try {
               listener.onRegister(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing exit event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               active.take(process);
               listener.onExit(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing exit event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onWriteError(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing write error event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onWriteOutput(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing write output event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onScriptError(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing syntax error event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onBegin(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing begin event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onProfile(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing profile event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onPong(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing pong event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onScope(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing scope event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onFault(channel, event);
            } catch(Exception e) {
               log.info(process + ": Exception processing fault event", e);
               listeners.remove(listener);
            }
         }
      }
   }
   
   private class ProcessAgentStarter implements Runnable {
      
      private final ProcessAgentPinger pinger;
      
      public ProcessAgentStarter(ProcessAgentPinger pinger) {
         this.pinger = pinger;
      }
      
      @Override
      public void run() {
         try {
            pinger.launch();
         }catch(Exception e) {
            log.info("Error starting agent", e);
         }
      }
   }
   
   private class ProcessAgentPinger implements Runnable {
   
      private final AtomicInteger listen;
      private final long frequency;
      
      public ProcessAgentPinger(long frequency) {
         this.listen = new AtomicInteger();
         this.frequency = frequency;
      }
      
      public void start(String host, int port) {
         if(listen.compareAndSet(0, port)) {
            Thread thread = factory.newThread(this);
            
            configuration.setHost(host);
            configuration.setPort(port);
            thread.start();
         }
      }
      
      @Override
      public void run() {
         while(true) {
            try {
               Thread.sleep(frequency);
               ping();
            }catch(Exception e) {
               log.info("Error pinging agents", e);
            }
         }
      }
      
      public boolean launch() {
         try {
            int port = listen.get();

            if(port != 0) {
               ProcessDefinition definition = launcher.launch(configuration);
               Process process = definition.getProcess();
               String name = definition.getName();
               
               manager.tail(process, name);
               return true;
            }
         }catch(Exception e) {
            log.info("Error launching agent", e);
         }
         return false;
      }
      
      public boolean kill() {
         try {
            int port = listen.get();

            if(port != 0) {
               ProcessConnection connection = waiting.acquire(filter);

               if(connection != null) {
                  String name = connection.toString();
                  
                  try {
                     log.debug(name + ": Killing process");
                     connection.close(name + ": Killing process due to over capacity");
                  }catch(Exception e) {
                     log.info("Error killing agent " + name, e);
                  } finally {
                     active.take(name);
                  }
               }
               return true;
            }
         }catch(Exception e) {
            log.info("Error killing agent", e);
         }
         return false;
      }
      
      private void ping() {
         try {
            pingWaiting();
            pingRunning();
         }catch(Exception e){
            log.info("Error pinging agents", e);
         }
      }
      
      private void pingWaiting() {
         long time = System.currentTimeMillis();
         
         try {
            List<ProcessConnection> notDead = new ArrayList<ProcessConnection>();

            while(!waiting.isEmpty()) {
               ProcessConnection connection = waiting.acquire(0);
               
               if(connection == null) {
                  break;
               }
               if(connection.ping(time)) {
                  notDead.add(connection);
               } else {
                  String process = connection.getProcess();
                  
                  log.info("Dead connection " + process);
                  active.take(process);
               }
            }
            waiting.register(notDead);
         }catch(Exception e){
            log.info("Error pinging agents", e);
         }
      }
      
      private void pingRunning() {
         long time = System.currentTimeMillis();         
         int require = capacity;
         int runningPool = running.size();
         int waitingPool = waiting.size();
         int remaining = require - waitingPool; 
         
         try {
            List<ProcessConnection> notDead = new ArrayList<ProcessConnection>();
            List<String> activeNames = new ArrayList<String>();
            
            if(remaining > 0) {
               launch(); // launch a new process at a time
            }
            if(remaining < 0 && require > 0) {
               kill(); // kill if pool grows too large
            }
            log.debug("Ping has " + waitingPool + " idle and " + runningPool+ " active from " + require) ;
            
            while(!running.isEmpty()) {
               ProcessConnection connection = running.poll();
               
               if(connection == null) {
                  break;
               }
               if(connection.ping(time)) {
                  String process = connection.getProcess();
                  activeNames.add(process);
                  notDead.add(connection);
               } else {
                  String process = connection.getProcess();
                  
                  log.info("Dead connection " + process);
                  active.take(process);
               }
            }
            running.addAll(notDead);
            log.info("Active processes are " + activeNames);;
         }catch(Exception e){
            log.info("Error pinging agents", e);
         }
      }
   }
}