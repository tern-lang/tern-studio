package org.ternlang.studio.agent.core;

public class ExecuteData {

   private final String dependencies;
   private final String process;
   private final String resource;
   private final String project;
   private final boolean debug;
   
   public ExecuteData(String process, String project, String resource, String dependencies, boolean debug) {
      this.dependencies = dependencies;
      this.project = project;
      this.resource = resource;
      this.process = process;
      this.debug = debug;
   }
   
   public String getDependencies() {
      return dependencies;
   }

   public String getProcess() {
      return process;
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
}