package org.ternlang.studio.agent.event;

public class StepEvent implements ProcessEvent {
   
   public static final int RUN = 0;
   public static final int STEP_IN = 1;
   public static final int STEP_OVER = 2;
   public static final int STEP_OUT = 3;
   
   private final String process;
   private final String thread;
   private final int type;
   
   private StepEvent(Builder builder) {
      this.process = builder.process;
      this.thread = builder.thread;
      this.type = builder.type;
   }

   @Override
   public String getProcess() {
      return process;
   }
   
   public String getThread() {
      return thread;
   }
   
   public int getType() {
      return type;
   }
   
   public static class Builder {
      
      private String process;
      private String thread;
      private int type;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withType(int type) {
         this.type = type;
         return this;
      }
      
      public StepEvent build(){
         return new StepEvent(this);
      }
   }

}