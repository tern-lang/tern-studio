package org.ternlang.studio.agent.profiler;

import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.studio.agent.event.ProcessEventChannel;

public class ProfileResultUpdater implements Runnable {

   private final AtomicReference<String> reference;
   private final ProcessEventChannel channel;
   private final TraceProfiler profiler;
   private final ThreadFactory factory;

   public ProfileResultUpdater(TraceProfiler profiler, ProcessEventChannel channel) {
      this.reference = new AtomicReference<String>();
      this.factory = new ThreadBuilder();
      this.profiler = profiler;
      this.channel = channel;
   }
   
   public void start(String process) {
      if(reference.compareAndSet(null, process)) {
         Thread thread = factory.newThread(this);
         thread.start();
      }
   }

   @Override
   public void run() {
      long delay = 1000;
      
      while(true) {
         String process = reference.get();
         try {
            Thread.sleep(delay);
            Set<ProfileResult> results = profiler.lines(2000);
            ProfileEvent event = new ProfileEvent.Builder(process)
               .withResults(results)
               .build();
            
            channel.send(event);
         }catch(Exception e) {
            e.printStackTrace();
         }finally{
            delay = 5000;
         }
      }
   }
}