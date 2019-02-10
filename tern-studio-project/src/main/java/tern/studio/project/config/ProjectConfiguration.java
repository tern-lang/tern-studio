package tern.studio.project.config;

import java.util.List;
import java.util.Map;

import tern.studio.project.ProjectLayout;

public interface ProjectConfiguration {
   
   String PROJECT_FILE = ".project";
   String CLASSPATH_FILE = ".classpath";
   String INDEX_FILE = ".index";
   
   List<Dependency> getDependencies();
   Map<String, String> getProperties();
   <T> T getAttribute(String name);
   void setAttribute(String name, Object value);
   ProjectLayout getProjectLayout();
   long getLastModifiedTime();
}
