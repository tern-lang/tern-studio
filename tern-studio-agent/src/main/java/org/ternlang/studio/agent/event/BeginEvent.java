package org.ternlang.studio.agent.event;

import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.core.ExecuteStatus;

public class BeginEvent implements StatusEvent {

   private final ExecuteStatus status;
   private final ProcessMode mode;
   private final String resource;
   private final String process;
   private final String project;
   private final String system;
   private final String pid;
   private final long totalMemory;
   private final long usedMemory;
   private final int threads;
   private final long duration;
   
   private BeginEvent(Builder builder) {
      this.totalMemory = builder.totalMemory;
      this.usedMemory = builder.usedMemory;
      this.threads = builder.threads;
      this.resource = builder.resource;
      this.process = builder.process;
      this.project = builder.project;
      this.status = builder.status;
      this.system = builder.system;
      this.duration = builder.duration;
      this.mode = builder.mode;
      this.pid = builder.pid;
   }   

   @Override
   public String getProcess() {
      return process;
   }
   
   public ProcessMode getMode() {
      return mode;
   }
   
   @Override
   public ExecuteStatus getStatus() {
      return status;
   }

   @Override
   public String getPid() {
      return pid;
   }
   
   @Override
   public String getProject() {
      return project;
   }

   @Override
   public String getSystem() {
      return system;
   }
   
   @Override
   public String getResource() {
      return resource;
   }
   
   @Override
   public long getUsedMemory() {
      return usedMemory;
   }

   @Override
   public long getTotalMemory() {
      return totalMemory;
   }

   @Override
   public int getThreads() {
      return threads;
   }
   
   public long getDuration() {
      return duration;
   }

   public static class Builder implements StatusEvent.Builder<BeginEvent> {
      
      private ExecuteStatus status;
      private ProcessMode mode;
      private String resource;
      private String process;
      private String project;
      private String system;
      private String pid;
      private long totalMemory;
      private long usedMemory;
      private int threads;
      private long duration;
      
      public Builder(String process){
         this.process = process;
      }
      
      @Override
      public Builder withProject(String project) {
         this.project = project;
         return this;
      }
      
      @Override
      public Builder withStatus(ExecuteStatus status) {
         this.status = status;
         return this;
      }

      @Override
      public Builder withResource(String resource) {
         this.resource = resource;
         return this;
      }

      @Override
      public Builder withPid(String pid) {
         this.pid = pid;
         return this;
      }

      @Override
      public Builder withSystem(String system) {
         this.system = system;
         return this;
      }
      
      @Override
      public Builder withThreads(int threads){
         this.threads = threads;
         return this;
      }
      
      @Override
      public Builder withTotalMemory(long totalMemory){
         this.totalMemory = totalMemory;
         return this;
      }
      
      @Override
      public Builder withUsedMemory(long usedMemory){
         this.usedMemory = usedMemory;
         return this;
      }
      
      public Builder withMode(ProcessMode mode) {
         this.mode = mode;
         return this;
      }

      public Builder withDuration(long duration) {
         this.duration = duration;
         return this;
      }

      public BeginEvent build(){
         return new BeginEvent(this);
      }            
   }
}