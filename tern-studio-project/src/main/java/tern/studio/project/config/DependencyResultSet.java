package tern.studio.project.config;

import java.util.List;

public class DependencyResultSet {

   private final List<DependencyResult> results;
   private final String message;
   private final String dependency;

   public DependencyResultSet(List<DependencyResult> results, String dependency) {
      this(results, dependency, null);
   }
   
   public DependencyResultSet(List<DependencyResult> results, String dependency, String message) {
      this.results = results;
      this.message = message;
      this.dependency = dependency;
   }
   
   public List<DependencyResult> getResults() {
      return results;
   }
   
   public String getDependency(){ 
      return dependency;
   }
   
   public String getMessage() {
      return message;
   }
   
   @Override
   public String toString() {
      return String.format("%s: %s", dependency, message);
   }
}
