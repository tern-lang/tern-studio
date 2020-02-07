package org.ternlang.studio.agent.debug;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.agent.message.common.Breakpoint;
import org.ternlang.agent.message.common.BreakpointArray;
import org.ternlang.agent.message.common.Line;
import org.ternlang.agent.message.common.LineArray;

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
   
   public void update(BreakpointArray breakpoints) {
      Set[] copy = new Set[1024];
      
      if(breakpoints != null) {
         for(Breakpoint breakpoint : breakpoints) {
            String resource = breakpoint.resource().toString();
            LineArray lines = breakpoint.lines();

            for(Line line : lines) {
               int number = line.line();

               if(line.active()) {
                  if(number > copy.length) {
                     copy = copyOf(copy, number * 2);
                  }
                  Set set = copy[number];

                  if(set == null) {
                     set = new HashSet();
                     copy[number] = set;
                  }
                  String module = ResourceExtractor.extractModule(resource);

                  set.add(module); // add module
                  set.add(resource); // add module resource file

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