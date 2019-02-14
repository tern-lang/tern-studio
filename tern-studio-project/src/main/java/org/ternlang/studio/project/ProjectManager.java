package org.ternlang.studio.project;

import static org.ternlang.studio.project.Workspace.createDefaultFile;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.ternlang.studio.project.config.ConfigurationReader;
import org.ternlang.studio.project.generate.ConfigFileSource;

@Slf4j
public class ProjectManager {
   
   private static final String DEFAULT_PROJECT = "default";

   private final Map<String, Project> projects;
   private final ConfigurationReader reader;
   private final ConfigFileSource source;
   private final Workspace workspace;
   private final ProjectMode mode;
   private final Project single;
   
   public ProjectManager(ConfigurationReader reader, ConfigFileSource source, Workspace workspace, ProjectMode mode){
      this.projects = new ConcurrentHashMap<String, Project>();
      this.single = new Project(reader, source, workspace, ".", DEFAULT_PROJECT);
      this.workspace = workspace;
      this.source = source;
      this.reader = reader;
      this.mode = mode;
   }
   
   public File getRoot() {
      return workspace.createWorkspace();
   }
   
   public Project getProject(String name){ 
      return projects.get(name);
   }
   
   public Project getProject(Path path){ // /project/<project-name>/ || /project/default
      if(mode.isMultipleMode()) { // multiple project support
         String projectPrefix = path.getPath(1, 1); // /<project-name>
         String projectName = projectPrefix.substring(1); // <project-name>
         
         return projects.get(projectName);
      }
      return single;
   }
   
   public Project createProject(Path path){ // /project/<project-name>/ || /project/default
      if(mode.isMultipleMode()) { // multiple project support
         String projectPrefix = path.getPath(1, 1); // /<project-name>
         String projectName = projectPrefix.substring(1); // <project-name>

         return createProject(projectName);
      }
      return single;
   }
   
   public Project createProject(String projectName){
      if(mode.isMultipleMode()) { 
         Project project = projects.get(projectName);
         
         if(project == null) {
            project = new Project(reader, source, workspace, projectName, projectName);
            projects.put(projectName, project);
         }
         File file = project.getBasePath();
         
         if(!file.exists()) {
            file.mkdirs();
            createDefaultProject(file);
         }
         return project;
      }
      return single;
   }
   
   private void createDefaultProject(File file) {
      try {
         File directory = file.getCanonicalFile();
         
         if(!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not build project directory " + directory);
         }
         createDefaultFile(directory, ".gitignore", "/.project\n/.classpath\n/.index\n");
         createDefaultFile(directory, ".project", "<project></project>");
      }catch(Exception e) {
         log.info("Could not create default project at '" + file + "'", e);
      }
   }
}