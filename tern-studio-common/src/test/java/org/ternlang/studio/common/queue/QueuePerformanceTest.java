package org.ternlang.studio.common.queue;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

public class QueuePerformanceTest extends TestCase {

   private static final int ITERATIONS = 100000000;
   
   private static interface Exchanger<T> {
      void offer(T object);
      T take();
      int size();
   }
   
   private static class ConcurrentExchanger<T> implements Exchanger<T> {

      private final Queue<T> queue = new ConcurrentLinkedQueue<T>();
      
      @Override
      public void offer(T object) {
         queue.offer(object);
      }
      
      @Override
      public T take() {
         return queue.poll();
      }
      
      @Override
      public int size() {
         return queue.size();
      }
   }
   
   private static class BlockingExchanger<T> implements Exchanger<T> {

      private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
      
      @Override
      public void offer(T object) {
         queue.offer(object);
      }
      
      @Override
      public T take() {
         return queue.poll();
      }
      
      @Override
      public int size() {
         return queue.size();
      }
   }
   
   private static class IdleStrategy {
      
      public void idle() {
         Thread.yield();
      }
   }
   
   private static class Consumer extends Thread {
      
      private final Exchanger<String> queue;
      private final IdleStrategy strategy;
      private final String name;
      
      public Consumer(String name, Exchanger<String> queue, IdleStrategy strategy) {
         this.strategy = strategy;
         this.queue = queue;
         this.name = name;
      }
      
      @Override
      public void run() {
         long time = System.currentTimeMillis();
         int count = 0;
         
         while(count < ITERATIONS) {
            Object value = queue.take();
            
            if(value == null) {
               strategy.idle();
            } else {
               count++;
            }
         }
         long duration = System.currentTimeMillis() - time;
         System.err.println("["+name+"] consume time=" + duration);
      }
   }
   
   private static class Producer extends Thread {
      
      private final Exchanger<String> queue;
      private final IdleStrategy strategy;
      private final String name;
      private final int limit;
      
      public Producer(String name, Exchanger<String> queue, IdleStrategy strategy, int limit) {
         this.strategy = strategy;
         this.queue = queue;
         this.limit = limit;
         this.name = name;
      }
      
      @Override
      public void run() {
         long time = System.currentTimeMillis();
         String value = "foo";
         int count = 0;
         
         while(count < ITERATIONS) {
            int size = queue.size();
            
            if(size < limit) {
               queue.offer(value);
               count++;
            } else {
               strategy.idle();
            }
         }
         long duration = System.currentTimeMillis() - time;
         System.err.println("["+name+"] produce time=" + duration);
      }
   }
   
   public void testQueues() throws Exception {
      perfTest("concurrent", new ConcurrentExchanger<String>());
      perfTest("blocking", new BlockingExchanger<String>());
   }
   
   public static void perfTest(final String name, final Exchanger<String> exchanger) throws Exception {
      final IdleStrategy strategy = new IdleStrategy();
      Producer producer = new Producer(name, exchanger, strategy, 100000);
      Consumer consumer = new Consumer(name, exchanger, strategy);
      
      consumer.start();
      producer.start();
      consumer.join();
      producer.join();
   }
}
