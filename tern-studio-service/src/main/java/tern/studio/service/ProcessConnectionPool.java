package tern.studio.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessConnectionPool {

   private final BlockingQueue<ProcessConnection> pool;
   
   public ProcessConnectionPool(){
      this.pool = new LinkedBlockingQueue<ProcessConnection>();
   }
   
   public ProcessConnection acquire(long wait) {
      try {
         return pool.poll(wait, TimeUnit.MILLISECONDS); // take a process from the pool
      }catch(Exception e){
         log.info("Could not acquire process", e);
      }
      return null;
   }
   
   public ProcessConnection acquire(long wait, String process) {
      try {
         if(process == null) {
            return acquire(wait);
         }
         int count = 10 * pool.size();
         
         while(count-- > 0) {
            ProcessConnection connection = pool.poll(5, TimeUnit.MILLISECONDS); // take a process from the pool
            
            if(connection != null) {
               String name = connection.getProcess();
               
               if(name.equals(process)) { 
                  return connection; // if there is a match then return it
               }
               pool.offer(connection);
            }
         }
      }catch(Exception e){
         log.info("Could not acquire process '" +process+ "'", e);
      }
      return null;
   }
   
   public ProcessConnection acquire(ProcessNameFilter filter) {
      try {
         if(filter != null) {
            int count = 10 * pool.size();
            
            while(count-- > 0) {
               ProcessConnection connection = pool.poll(5, TimeUnit.MILLISECONDS); // take a process from the pool
               
               if(connection != null) {
                  String name = connection.getProcess();
                  
                  if(filter.accept(name)) { 
                     return connection; // if there is a match then return it
                  }
                  pool.offer(connection);
               }
            }
         }
      }catch(Exception e){
         log.info("Could not acquire process", e);
      }
      return null;
   }
   
   public void register(ProcessConnection connection) {
      pool.offer(connection);
      
   }
   
   public void register(List<ProcessConnection> connections) {
      for(ProcessConnection connection : connections){
         register(connection);
      }
   }
   
   public boolean isEmpty(){
      return pool.isEmpty();
   }
   
   public int size() {
      return pool.size();
   }
}