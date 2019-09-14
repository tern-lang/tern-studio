package org.ternlang.studio.project.config;

import java.util.List;
import java.util.Map;

public interface WorkspaceConfiguration {

   String CLASS_EXTENSION = ".class";
   String WORKSPACE_FILE = ".workspace";
   String INDEX_PATH = "index";
   String BACKUP_PATH = "backup";
   String TEMP_PATH = "temp";
   String JAR_FILE = "agent.jar";
   
   List<DependencyFile> getDependencies(List<Dependency> dependencies);
   Map<String, String> getEnvironmentVariables();
   List<String> getArguments();
   long getTimeLimit();
}