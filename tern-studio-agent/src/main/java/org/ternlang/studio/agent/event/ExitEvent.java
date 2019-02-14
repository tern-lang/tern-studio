package org.ternlang.studio.agent.event;

import org.ternlang.studio.agent.ProcessMode;

public class ExitEvent implements ProcessEvent {

   private final ProcessMode mode;
   private final String process;
   private final long duration;

   private ExitEvent(Builder builder) {
      this.duration = builder.duration;
      this.process = builder.process;
      this.mode = builder.mode;
   }
   
   public ProcessMode getMode() {
      return mode;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getDuration() { // execute time
      return duration;
   }

   public static class Builder {
      
      private ProcessMode mode;
      private String process;
      private long duration;
      
      public Builder(String process) {
         this.process = process;
      }
      
      public Builder withMode(ProcessMode mode) {
         this.mode = mode;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withDuration(long duration) {
         this.duration = duration;
         return this;
      }
      
      public ExitEvent build(){
         return new ExitEvent(this);
      }
      
   }
}