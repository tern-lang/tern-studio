package org.ternlang.studio.agent.debug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BreakpointMatcher {

   private volatile AtomicBoolean suspend;
   private volatile Set[] matches;
   
   public BreakpointMatcher() {
      this.suspend = new AtomicBoolean();
      this.matches = new Set[0];
   }   
   
   public void suspend() {
      suspend.set(true);
   }
   
   public void update(Map<String, Map<Integer, Boolean>> breakpoints) {
      Set[] copy = new Set[1024];
      
      if(breakpoints != null) {
         Set<String> resources = breakpoints.keySet();
         
         for(String resource : resources) {
            Map<Integer, Boolean> locations = breakpoints.get(resource);
            
            if(locations != null) {
               Set<Integer> lines = locations.keySet();
               
               for(Integer line : lines) {
                  Boolean enabled = locations.get(line); 
                        
                  if(enabled != null && enabled.booleanValue()) {
                     if(line > copy.length) {
                        copy = copyOf(copy, line * 2);
                     }
                     Set set = copy[line];
                     
                     if(set == null) {
                        set = new HashSet();
                        copy[line] = set;
                     }
                     String module = ResourceExtractor.extractModule(resource);
                     
                     set.add(module); // add module 
                     set.add(resource); // add module resource file
                     
                  }
               }
            }
         }
      }
      matches = copy;
   }
   
   public boolean isBreakpoint(String resource, int line) {
      if(line < matches.length) {
         if(line >= 0) {
            Set set = matches[line];
         
            if(set != null) {
               return set.contains(resource) || suspend.getAndSet(false);
            }
         }
      }
      return suspend.getAndSet(false);
   }
   
   private Set[] copyOf(Set[] array, int newSize) {
      Set[] copy = new Set[newSize];
      System.arraycopy(array, 0, copy, 0, Math.min(newSize, array.length));
      return copy;
   }
}