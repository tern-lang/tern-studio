package org.ternlang.studio.agent.event;

import java.util.List;
import java.util.Map;

import org.ternlang.studio.agent.core.ExecuteData;

public class ExecuteEvent implements ProcessEvent {

   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final List<String> arguments;
   private final ExecuteData data;
   private final String dependencies;
   private final String project;
   private final String resource;
   private final String process;
   private final boolean debug;
   
   private ExecuteEvent(Builder builder) {
      this.data = new ExecuteData(builder.process, builder.project, builder.resource, builder.dependencies, builder.debug);
      this.dependencies = builder.dependencies;
      this.breakpoints = builder.breakpoints;
      this.arguments = builder.arguments;
      this.project = builder.project;
      this.resource = builder.resource;
      this.process = builder.process;
      this.debug = builder.debug;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public ExecuteData getData() {
      return data; 
   }
   
   public List<String> getArguments() {
      return arguments;
   }
   
   public Map<String, Map<Integer, Boolean>> getBreakpoints() {
      return breakpoints;
   }
   
   public String getDependences() {
      return dependencies;
   }
   
   public String getResource() {
      return resource;
   }
   
   public String getProject() {
      return project;
   }
   
   public boolean isDebug(){
      return debug;
   }
   
   public static class Builder {
      
      private Map<String, Map<Integer, Boolean>> breakpoints;
      private List<String> arguments;
      private String dependencies;
      private String project;
      private String resource;
      private String process;
      private boolean debug;
      
      public Builder(String process) {
         this.process = process;
      }
      
      public Builder withArguments(List<String> arguments) {
         this.arguments = arguments;
         return this;
      }

      public Builder withBreakpoints(Map<String, Map<Integer, Boolean>> breakpoints) {
         this.breakpoints = breakpoints;
         return this;
      }
      
      public Builder withDependencies(String dependencies) {
         this.dependencies = dependencies;
         return this;
      }

      public Builder withProject(String project) {
         this.project = project;
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
      
      public Builder withDebug(boolean debug) {
         this.debug = debug;
         return this;
      }
      
      public ExecuteEvent build(){
         return new ExecuteEvent(this);
      }
   }
}