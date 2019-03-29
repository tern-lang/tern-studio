package org.ternlang.studio.service.agent.worker;

import org.springframework.stereotype.Component;
import org.ternlang.studio.agent.worker.WorkerNameGenerator;
import org.ternlang.studio.service.ProcessNameFilter;

@Component
public class WorkerProcessNameFilter implements ProcessNameFilter {
   
   private static final String PROCESS_PREFIX = "agent";
   
   private final WorkerNameGenerator generator;
   
   public WorkerProcessNameFilter(){
      this(PROCESS_PREFIX);
   }
   
   public WorkerProcessNameFilter(String prefix){
      this.generator = new WorkerNameGenerator(prefix);
   }
   
   @Override
   public String generate() {
      return generator.getName();
   }

   @Override
   public boolean accept(String name) {
      String prefix = generator.getPrefix();
      
      if(name != null) {
         return name.matches(prefix + "\\-\\d+");
      }
      return false;
   }
}