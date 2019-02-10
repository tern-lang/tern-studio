package tern.studio.agent.event;

import tern.studio.agent.debug.ScopeVariableTree;

public class FaultEvent implements ProcessEvent {

   private final ScopeVariableTree variables;
   private final String process;
   private final String resource;
   private final String thread;
   private final String cause;
   private final int line;
   
   private FaultEvent(Builder builder) {
      this.variables = builder.variables;
      this.resource = builder.resource;
      this.process = builder.process;
      this.thread = builder.thread;
      this.cause = builder.cause;
      this.line = builder.line;
   }
   
   @Override
   public String getProcess() {
      return process;
   }

   public ScopeVariableTree getVariables() {
      return variables;
   }

   public String getCause() {
      return cause;
   }
   
   public String getResource() {
      return resource;
   }

   public String getThread() {
      return thread;
   }

   public int getLine() {
      return line;
   }
   
   public static class Builder {
      
      private ScopeVariableTree variables;
      private String process;
      private String resource;
      private String thread;
      private String cause;
      private int line;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withVariables(ScopeVariableTree variables) {
         this.variables = variables;
         return this;
      }

      public Builder withCause(String cause) {
         this.cause = cause;
         return this;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      public Builder withThread(String thread) {
         this.thread = thread;
         return this;
      }

      public Builder withLine(int line) {
         this.line = line;
         return this;
      }
      
      public FaultEvent build() {
         return new FaultEvent(this);
      }
   }
}