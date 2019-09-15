package org.ternlang.studio.agent.event;

import org.ternlang.studio.agent.core.ExecuteStatus;

public class PongEvent implements StatusEvent {

   private final ExecuteStatus status;
   private final String project;
   private final String process;
   private final String resource;
   private final String system;
   private final String pid;
   private final long totalTime;
   private final long usedTime;
   private final long totalMemory;
   private final long usedMemory;
   private final int threads;

   protected PongEvent(Builder<? extends PongEvent> builder) {
      this.totalMemory = builder.totalMemory;
      this.usedMemory = builder.usedMemory;
      this.totalTime = builder.totalTime;
      this.usedTime = builder.usedTime;
      this.threads = builder.threads;
      this.resource = builder.resource;
      this.process = builder.process;
      this.project = builder.project;
      this.status = builder.status;
      this.system = builder.system;
      this.pid = builder.pid;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   @Override
   public ExecuteStatus getStatus() {
      return status;
   }
   
   @Override
   public String getProject() {
      return project;
   }

   @Override
   public String getPid(){
      return pid;
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
   public long getUsedTime() {
      return usedTime;
   }

   @Override
   public long getTotalTime() {
      return totalTime;
   }
   
   @Override
   public int getThreads() {
      return threads;
   }
   
   public static class Builder<T extends PongEvent> implements StatusEvent.Builder<T> {
      
      private ExecuteStatus status;
      private String project;
      private String process;
      private String resource;
      private String system;
      private String pid;
      private long totalTime;
      private long usedTime;
      private long totalMemory;
      private long usedMemory;
      private int threads;
   
      public Builder(String process) {
         this.process = process;
      }

      @Override
      public Builder<T> withProject(String project) {
         this.project = project;
         return this;
      }
      
      @Override
      public Builder<T> withStatus(ExecuteStatus status) {
         this.status = status;
         return this;
      }

      @Override
      public Builder<T> withResource(String resource) {
         this.resource = resource;
         return this;
      }

      @Override
      public Builder<T> withPid(String pid) {
         this.pid = pid;
         return this;
      }

      @Override
      public Builder<T> withSystem(String system) {
         this.system = system;
         return this;
      }
      
      @Override
      public Builder<T> withThreads(int threads){
         this.threads = threads;
         return this;
      }
      
      @Override
      public Builder<T> withTotalMemory(long totalMemory){
         this.totalMemory = totalMemory;
         return this;
      }
      
      @Override
      public Builder<T> withUsedMemory(long usedMemory){
         this.usedMemory = usedMemory;
         return this;
      }
      
      @Override
      public Builder<T> withTotalTime(long totalTime){
         this.totalTime = totalTime;
         return this;
      }
      
      @Override
      public Builder<T> withUsedTime(long usedTime){
         this.usedTime = usedTime;
         return this;
      }
      
      @Override
      public T build() {
         return (T)new PongEvent(this);
      }
   }
}