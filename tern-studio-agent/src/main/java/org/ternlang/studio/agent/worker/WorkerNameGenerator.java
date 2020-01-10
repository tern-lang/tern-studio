package org.ternlang.studio.agent.worker;

import java.util.concurrent.atomic.AtomicLong;

import org.ternlang.common.DateFormatter;

public class WorkerNameGenerator {

   public static final String DEFAULT_PREFIX = "local";
   public static final String DATE_FORMAT = "ddHHmmss";
   
   private final AtomicLong counter;
   private final String prefix;
   
   public WorkerNameGenerator(){
      this(DEFAULT_PREFIX);
   }
   
   public WorkerNameGenerator(String prefix){
      this.counter = new AtomicLong(1);
      this.prefix = prefix;
   }
   
   public String getPrefix() {
      return prefix;
   }
   
   public String getName() {
      long time = System.currentTimeMillis();
      long sequence = counter.getAndIncrement();
      String date = DateFormatter.format(DATE_FORMAT, time);
      
      return String.format("%s-%s%s", prefix, sequence, date);
   }
}

