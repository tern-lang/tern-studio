package org.ternlang.studio.agent.event;

import java.util.Collections;
import java.util.Map;

public class BreakpointsEvent implements ProcessEvent {

   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final String process;
   
   private BreakpointsEvent(Builder builder) {
      this.breakpoints = Collections.unmodifiableMap(builder.breakpoints);
      this.process = builder.process;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
   
   public static class Builder {
      
      private Map<String, Map<Integer, Boolean>> breakpoints;
      private String process;
      
      public Builder(String process){
         this.process = process;
      }

      public Builder withBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
         this.breakpoints = breakpoints;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public BreakpointsEvent build() {
         return new BreakpointsEvent(this);
      }
   }
}