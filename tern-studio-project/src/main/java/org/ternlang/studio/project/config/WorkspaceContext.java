package org.ternlang.studio.project.config;

import java.util.List;
import java.util.Map;

import org.ternlang.studio.project.maven.RepositoryFactory;

public class WorkspaceContext implements WorkspaceConfiguration {

   private final Map<String, String> variables;
   private final List<String> arguments;
   private final RepositoryFactory factory;
   private final DependencyLoader loader;
   
   public WorkspaceContext(RepositoryFactory factory, DependencyLoader loader, Map<String, String> variables, List<String> arguments){
      this.variables = variables;
      this.arguments = arguments;
      this.loader = loader;
      this.factory = factory;
   }

   @Override
   public List<DependencyFile> getDependencies(List<Dependency> dependencies) {
      if(loader == null) {
         throw new IllegalStateException("Could not resolve dependencies");
      }
      return loader.getDependencies(factory, dependencies);
   }
   
   @Override
   public Map<String, String> getEnvironmentVariables() {
      return variables;
   }

   @Override
   public List<String> getArguments() {
      return arguments;
   }
}