package org.ternlang.studio;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import lombok.SneakyThrows;

public class TickTock {

   @SneakyThrows
   public static void main(String[] list) {
      final TickTockServer server = new TickTockServer(1992);
      final TickTockClient client = new TickTockClient("localhost", 1992);
      final BlockingQueue<Long> queue = new ArrayBlockingQueue<>(1000);

      new Thread(() -> {
         while (true) {
            try {
               System.out.println(queue.take());
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }).start();

      server.start();
      client.start(queue::offer);
   }

   static class TickTockClock {

      static long timeMicros() {
         return ChronoUnit.MICROS.between(Instant.EPOCH, Instant.now());
      }

      static void sleepMicros(long duration) {
         LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(duration));
      }
   }

   static class TickTockClient {

      final Averager averager;
      final Socket socket;

      @SneakyThrows
      TickTockClient(String host, int port) {
         this.socket = new Socket(host, port);
         this.averager = new Averager(50);
      }

      @SneakyThrows
      void start(Consumer<Long> listener) {
         final InputStream input = socket.getInputStream();
         final OutputStream output = socket.getOutputStream();
         final DataInputStream reader = new DataInputStream(input);
         final DataOutputStream writer = new DataOutputStream(output);
         final long second = TimeUnit.SECONDS.toMicros(1);
         final long spinTime = TimeUnit.MILLISECONDS.toMicros(10);
         final long startTime = TickTockClock.timeMicros(); // current local time

         socket.setTcpNoDelay(true);
         writer.writeLong(startTime);
         writer.flush();

         while (true) {
            final long remoteTime = reader.readLong(); // read remote time
            final long accuracy = reader.readLong(); // read remote accuracy
            final long localTime = TickTockClock.timeMicros(); // current local time
            final long averageDiff = averager.average(); // average difference 80th percentile

            if (averageDiff > 0) {
               final long sleep = Math.max(0, averageDiff - spinTime);

               if (sleep > 0 && sleep < spinTime) {
                  TickTockClock.sleepMicros(sleep); // sleep until 20 ms before time
               }
            }
            final long pingTime = (localTime + averageDiff) + second; // what time will result in match
            final long nextTime = remoteTime + second;

            while (true) {
               final long currentTime = TickTockClock.timeMicros();

               if (currentTime >= pingTime) { // spin until difference is exact
                  writer.writeLong(nextTime);
                  writer.flush();
                  break;
               }
            }
            averager.update(-accuracy);
            listener.accept(accuracy); // report on accuracy should be async
         }
      }

      static class Averager {

         final PriorityQueue<Long> queue;
         final int capacity;
         final ArrayDeque<Long> samples;

         Averager(int capacity) {
            this.queue = new PriorityQueue<>();
            this.samples = new ArrayDeque<>(capacity);
            this.capacity = capacity;
         }

         long update(long sample) {
            if (samples.size() >= capacity) {
               samples.poll();
            }
            samples.offer(sample);

            return average();
         }

         long average() {
            long tenPercent = Math.round(samples.size() * 0.1);
            long eightyPercent = samples.size() - (tenPercent * 2);

            if (eightyPercent > 0) {
               long total = 0;

               queue.clear();
               queue.addAll(samples);

               for (int i = 0; i < tenPercent; i++) { // remove lowest 10%
                  queue.poll();
               }
               for (int i = 0; i < eightyPercent; i++) { // ignore highest 10%
                  total += queue.poll();
               }
               return total / eightyPercent;
            }
            return 0;
         }
      }
   }

   static class TickTockServer {

      final ServerSocket server;

      @SneakyThrows
      TickTockServer(int port) {
         this.server = new ServerSocket(port);
      }

      void start() {
         new Thread(() -> accept()).start();
      }

      @SneakyThrows
      void accept() {
         while (true) {
            final Socket socket = server.accept();
            socket.setTcpNoDelay(true);
            new Thread(new TickTockAgent(socket)).start();
         }
      }
   }

   static class TickTockAgent implements Runnable {

      final long random; // random 5 second difference
      final Socket socket;

      @SneakyThrows
      TickTockAgent(Socket socket) {
         this.random = new SecureRandom().nextInt(500000);
         this.socket = socket;
      }

      @SneakyThrows
      public void run() {
         try {
            final InputStream input = socket.getInputStream();
            final OutputStream output = socket.getOutputStream();
            final DataOutputStream writer = new DataOutputStream(output);
            final DataInputStream reader = new DataInputStream(input);

            socket.setTcpNoDelay(true);

            while (true) {
               final long expectTime = reader.readLong();
               final long localTime = TickTockClock.timeMicros();
               final long accuracy = localTime - expectTime;

               writer.writeLong(localTime);
               writer.writeLong(accuracy);
               writer.flush();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

}
