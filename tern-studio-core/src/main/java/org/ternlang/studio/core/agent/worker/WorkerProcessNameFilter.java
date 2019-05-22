package org.ternlang.studio.core.agent.worker;

import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.agent.worker.WorkerNameGenerator;
import org.ternlang.studio.core.ProcessNameFilter;

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