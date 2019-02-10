package tern.studio.agent.event;


public class PingEvent implements ProcessEvent {

   private final String process;
   private final long time;
   
   private PingEvent(Builder builder) {
      this.process = builder.process;
      this.time = builder.time;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public long getTime() {
      return time;
   }

   public static class Builder {
      
      private String process;
      private long time;
      
      public Builder(String process){
         this.process = process;
      }
      
      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }
      
      public Builder withTime(long time) {
         this.time = time;
         return this;
      } 
      
      public PingEvent build(){
         return new PingEvent(this);
      }
   }
}