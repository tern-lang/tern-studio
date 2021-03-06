package org.ternlang.studio.project.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EmptyConfiguration implements WorkspaceConfiguration {
   
   public EmptyConfiguration() {
      super();
   }

   @Override
   public List<DependencyFile> getDependencies(List<Dependency> dependencies) {
      return Collections.emptyList();
   }
   
   @Override
   public Map<String, String> getEnvironmentVariables() {
      return Collections.emptyMap();
   }

   @Override
   public List<String> getArguments() {
      return Collections.emptyList();
   }
   
   @Override
   public String getSecurityPolicy() {
      return null;
   }

   @Override
   public boolean isSecurityEnabled() {
      return false;
   }
   
   @Override
   public long getTimeLimit() {
      return TimeUnit.DAYS.toMillis(2);
   }
}