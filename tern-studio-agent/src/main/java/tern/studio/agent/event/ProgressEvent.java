package tern.studio.agent.event;

public class ProgressEvent extends PongEvent {

   protected ProgressEvent(Builder builder) {
      super(builder);
   }
   
   public static class Builder extends PongEvent.Builder<ProgressEvent> {
   
      public Builder(String process) {
         super(process);
      }
      
      @Override
      public ProgressEvent build() {
         return new ProgressEvent(this);
      }
   }
}