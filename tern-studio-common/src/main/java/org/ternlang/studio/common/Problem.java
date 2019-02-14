package org.ternlang.studio.common;

public class Problem {

   private final String description;
   private final String resource;
   private final String project;
   private final int line;
   
   public Problem(String project, String resource, String description, int line) {
      this.description = description;
      this.resource = resource;
      this.project = project;
      this.line = line;
   }
   
   public String getProject() {
      return project;
   }
   
   public String getDescription() {
      return description;
   }
   
   public String getResource() {
      return resource;
   }
   
   public int getLine() {
      return line;
   }
}