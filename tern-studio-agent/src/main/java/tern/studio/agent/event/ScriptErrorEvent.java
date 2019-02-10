package tern.studio.agent.event;

public class ScriptErrorEvent implements ProcessEvent {

   private final String description;
   private final String resource;
   private final String process;
   private final int line;
   
   private ScriptErrorEvent(Builder builder) {
      this.description = builder.description;
      this.process = builder.process;
      this.resource = builder.resource;
      this.line = builder.line;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public String getDescription(){
      return description;
   }
      
   public String getResource() {
      return resource;
   }

   public int getLine() {
      return line;
   }
   
   public static class Builder {
      
      private String description;
      private String resource;
      private String process;
      private int line;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withDescription(String description) {
         this.description = description;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withLine(int line) {
         this.line = line;
         return this;
      }
      
      public ScriptErrorEvent build(){
         return new ScriptErrorEvent(this);
      }
      
      
   }
}