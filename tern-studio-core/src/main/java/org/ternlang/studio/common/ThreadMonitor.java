package org.ternlang.studio.common;

import java.util.Map;
import java.util.Set;

public class ThreadMonitor {

   public static void start(long frequency) {
      ThreadDumper dumper = new ThreadDumper(frequency);
      dumper.start();
   }

   private static class ThreadDumper extends Thread {

      private final long frequency;

      public ThreadDumper(long frequency) {
         this.frequency = frequency;
      }

      public void run() {
         while (true) {
            try {
               Thread.sleep(frequency);
               dumpAllThreads();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      public void dumpAllThreads() {
         Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
         Set<Thread> threads = stackTraces.keySet();
         int count = threads.size();

         if (!threads.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            int index = 1;

            for (Thread thread : threads) {
               StackTraceElement[] traceElements = stackTraces.get(thread);
               String name = thread.getName();
               Thread.State state = thread.getState();

               builder.append("thread=");
               builder.append(name);
               builder.append(" state=");
               builder.append(state);
               builder.append(" (");
               builder.append(index++);
               builder.append(" of ");
               builder.append(count);
               builder.append(")");
               builder.append("\n");

               for (StackTraceElement traceElement : traceElements) {
                  builder.append("   ");
                  builder.append(traceElement);
                  builder.append("\n");
               }
               builder.append("\n");
            }
            String threadDump = builder.toString();

            System.err.println(threadDump);
         }
      }
   }
}
