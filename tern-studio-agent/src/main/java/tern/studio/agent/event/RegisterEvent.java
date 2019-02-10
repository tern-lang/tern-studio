package tern.studio.agent.event;

import static tern.studio.agent.core.ExecuteStatus.REGISTERING;

import tern.studio.agent.core.ExecuteStatus;

public class RegisterEvent implements ProcessEvent {

   private final String process;
   private final String system;
   private final String pid;
   
   private RegisterEvent(Builder builder) {
      this.process = builder.process;
      this.system = builder.system;
      this.pid = builder.pid;
   }
   
   @Override
   public String getProcess() {
      return process;
   }

   public String getPid() {
      return pid;
   }

   public String getSystem() {
      return system;
   }
   
   public ExecuteStatus getStatus() {
      return REGISTERING;
   }
   
   public static class Builder {
      
      private String process;
      private String system;
      private String pid;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withPid(String pid) {
         this.pid = pid;
         return this;
      }

      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }
      
      public RegisterEvent build(){
         return new RegisterEvent(this);
      }
   }
}