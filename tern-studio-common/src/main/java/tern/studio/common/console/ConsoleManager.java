package tern.studio.common.console;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import tern.common.thread.ThreadBuilder;

public class ConsoleManager {

   private final ConsoleListener listener;
   private final ConsoleChecker checker;
   private final ThreadFactory factory;

   public ConsoleManager(ConsoleListener listener) {
      this(listener, 5000);
   }
   
   public ConsoleManager(ConsoleListener listener, long frequency) {
      this.checker = new ConsoleChecker(frequency);
      this.factory = new ThreadBuilder();
      this.listener = listener;
   }
   
   public void tail(Process process, String name) {
      Console console = new Console(process, name);
      
      if(checker.isAlive()) {
         Thread thread = factory.newThread(console);
         
         checker.register(console);
         console.start();
         thread.start();
      }
   }
   
   public void start() {
      if(!checker.isAlive()) {
         Thread thread = factory.newThread(checker);
         
         checker.start();
         thread.start();
      }
   }
   
   public void stop() {
      checker.stop();
   }
   
   private class ConsoleChecker implements Runnable {
      
      private final Set<Console> consoles;
      private final AtomicBoolean active;
      private final long frequency;
      
      public ConsoleChecker(long frequency) {
         this.consoles = new CopyOnWriteArraySet<Console>();
         this.active = new AtomicBoolean();
         this.frequency = frequency;
      }
      
      public void register(Console console) {
         consoles.add(console);
      }
      
      public boolean isAlive() {
         return active.get();
      }
      
      public void stop() {
         active.set(false);
      }
      
      public void start() {
         active.set(true);
      }
      
      @Override
      public void run() {
         try {
            while(active.get()) {
               Thread.sleep(frequency);
               
               for(Console console : consoles) {
                  try {
                     if(!console.isAlive()) {
                        consoles.remove(console);
                        console.stop();
                     }
                  } catch(Exception e) {
                     consoles.remove(console);
                     console.stop();
                  }
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            active.get();
         }
      }
   }
   
   private class Console implements Runnable {
      
      private final AtomicBoolean active;
      private final Process process;
      private final String name;
      
      public Console(Process process, String name) {
         this.active = new AtomicBoolean();
         this.process = process;
         this.name = name;
      }
      
      public boolean isAlive() {
         return process.isAlive() && active.get();
      }
      
      public void stop() {
         active.set(false);
         process.destroyForcibly();
      }
      
      public void start() {
         active.set(true);
      }
      
      @Override
      public void run() {
         try {
            InputStream stream = process.getInputStream();
  
            while(active.get()) {
               String line = ConsoleReader.read(stream);
               
               if(line != null) {
                  listener.onUpdate(name, line);
               }
            }
         } catch(Exception e) {
            listener.onUpdate(name, "Console closed", e);
         } finally {
            process.destroyForcibly();
            listener.onUpdate(name, "Process terminated");
            active.set(false);
         }
      }
   }
}