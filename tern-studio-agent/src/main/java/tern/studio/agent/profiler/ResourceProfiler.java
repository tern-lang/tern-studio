package tern.studio.agent.profiler;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

public class ResourceProfiler {
   
   private volatile String resource;
   private volatile int[] counts;
   private volatile int[] visits;
   private volatile long[] start;
   private volatile long[] times;
   private volatile int max;

   public ResourceProfiler(String resource) {
      this.start = new long[500];
      this.counts = new int[500];
      this.times = new long[500];
      this.visits = new int[500];
      this.resource = resource;
      this.max = 0;
   }

   public void collect(SortedSet<ProfileResult> results, int size) {
      long localMax = max;
      long[] localTimes = times;
      int[] localVisits = visits;
      
      for(int i = 0; i < localMax && i < size; i++){
         if(localTimes[i] > 0) {
            long duration = TimeUnit.NANOSECONDS.toMillis(localTimes[i]);
            int visits = localVisits[i];
            results.add(new ProfileResult(resource, duration, visits, i));
         }
      }
   }
   
   public void enter(int line) {
      // thread local required, also recursion counter
      if(line >= 0) {
         if(times.length <= line) {
            counts = copyOf(counts, line + 50);
            times = copyOf(times, line + 50);
            start = copyOf(start, line + 50);
            visits = copyOf(visits, line + 50);
         }
         int currentCount = counts[line]++;// we just entered an instruction
     
         if(currentCount == 0) {
            start[line] = System.nanoTime(); // first instruction to enter
         }
         visits[line]++;
      }
   }

   public void exit(int line) {
      if(line >= 0) {
         int currentCount = --counts[line]; // exit instruction
   
         if(currentCount == 0) {
            times[line] += (System.nanoTime() - start[line]);
            start[line] = 0L; // reset as we are now at zero
         }
         if(line > max) {
            max=line;
         }
      }
   }
   
   private int[] copyOf(int[] array, int newSize) {
      int[] copy = new int[newSize];
      System.arraycopy(array, 0, copy, 0, Math.min(newSize, array.length));
      return copy;
   }
   
   private long[] copyOf(long[] array, int newSize) {
      long[] copy = new long[newSize];
      System.arraycopy(array, 0, copy, 0, Math.min(newSize, array.length));
      return copy;
   }
}