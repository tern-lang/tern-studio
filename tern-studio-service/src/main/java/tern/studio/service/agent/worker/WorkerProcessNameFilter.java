package tern.studio.service.agent.worker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import tern.studio.service.ProcessNameFilter;
import org.springframework.stereotype.Component;

@Component
public class WorkerProcessNameFilter implements ProcessNameFilter {
   
   private static final String PROCESS_PREFIX = "agent";
   private static final String DATE_FORMAT = "ddHHmmss";
   
   private final AtomicLong counter;
   private final DateFormat format;
   private final String prefix;
   
   public WorkerProcessNameFilter(){
      this(PROCESS_PREFIX);
   }
   
   public WorkerProcessNameFilter(String prefix){
      this.format = new SimpleDateFormat(DATE_FORMAT);
      this.counter = new AtomicLong(1);
      this.prefix = prefix;
   }
   
   @Override
   public synchronized String generate() {
      long time = System.currentTimeMillis();
      long sequence = counter.getAndIncrement();
      String date = format.format(time);
      
      return String.format("%s-%s%s", prefix, sequence, date);
   }

   @Override
   public synchronized boolean accept(String name) {
      if(name != null) {
         return name.matches(prefix + "\\-\\d+");
      }
      return false;
   }
}