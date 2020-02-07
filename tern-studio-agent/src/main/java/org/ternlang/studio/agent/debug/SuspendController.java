package org.ternlang.studio.agent.debug;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ternlang.agent.message.common.VariablePathArray;

public class SuspendController {
   
   private final Map<String, ResumeListener> listeners;
   private final Map<String, ScopeBrowser> browsers;
   private final Map<String, ResumeType> types;
   private final Map<String, Object> locks;
   
   public SuspendController() {
      this.listeners = new ConcurrentHashMap<String, ResumeListener>();
      this.browsers = new ConcurrentHashMap<String, ScopeBrowser>();
      this.types = new ConcurrentHashMap<String, ResumeType>();
      this.locks = new ConcurrentHashMap<String, Object>();
   }
   
   public void resume(ResumeType type) {
      Set<String> threads = listeners.keySet();
      
      for(String thread : threads) {
         try {
            resume(type, thread);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
   
   public void resume(ResumeType type, String thread) {
      Object lock = locks.get(thread);
      ResumeListener listener = listeners.remove(thread);
      
      synchronized(lock) {
         try {
            browsers.remove(thread); 
            
            if(listener != null) {
               types.put(thread, type);
               listener.resume(thread);
            }
            lock.notify();
         }catch(Exception e) {
            throw new IllegalStateException("Could not resume thread '" + thread + "'", e);
         }
      }
   }

   public ResumeType suspend(ResumeListener listener, ScopeBrowser browser) {
      String name = Thread.currentThread().getName();
      Object lock = locks.get(name);
      
      if(lock == null) {
         lock = new Object();
         locks.put(name, lock);
      }
      synchronized(lock) {
         try {
            browsers.put(name, browser);
            listeners.put(name, listener);
            lock.wait();
         }catch(Exception e) {
            throw new IllegalStateException("Could not suspend thread '" + name + "'", e);
         }
      }
      return types.remove(name); // resume in a specific way
   }
   
   public void browse(VariablePathArray expand, String thread) {
      Object lock = locks.get(thread);
      ScopeBrowser browser = browsers.get(thread);
      
      synchronized(lock) {
         try {
            if(browser != null) {
               browser.browse(expand);
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not browse thread '" + thread + "'", e);
         }
      }
   }
   
   public void evaluate(VariablePathArray expand, String thread, String expression, boolean refresh) {
      Object lock = locks.get(thread);
      ScopeBrowser browser = browsers.get(thread);
      
      synchronized(lock) {
         try {
            if(browser != null) {
               browser.evaluate(expand, expression, refresh);
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not evaluate '" + expression + "' for thread '" + thread + "'", e);
         }
      }
   }
}