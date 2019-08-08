package org.ternlang.studio.project;

import static org.ternlang.studio.project.config.WorkspaceConfiguration.WORKSPACE_FILE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.simpleframework.http.Path;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.DependsOn;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.module.path.ClassPath;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.studio.common.FileDirectorySource;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.project.config.ConfigurationReader;
import org.ternlang.studio.project.config.Dependency;
import org.ternlang.studio.project.config.DependencyFile;
import org.ternlang.studio.project.config.ProjectConfiguration;
import org.ternlang.studio.project.config.WorkspaceConfiguration;
import org.ternlang.studio.project.decompile.Decompiler;
import org.ternlang.studio.project.generate.ClassPathFileGenerator;
import org.ternlang.studio.project.generate.ConfigFileSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DependsOn(ClassPathFileGenerator.class)
public class Workspace implements FileDirectorySource {

   private final ConfigurationReader reader;
   private final ProjectManager manager;
   private final Set<String> loaded;
   private final Executor executor;
   private final ClassPath path;
   private final File logFile;
   private final String level;
   private final File root;
   
   public Workspace(
         ConfigFileSource source,
         ClassPath path,
         @Value("${directory}") File root, 
         @Value("${log-file}") File logFile, 
         @Value("${log-level}") String level, 
         @Value("${mode}") ProjectMode mode) 
   {
      this.loaded = new CopyOnWriteArraySet<String>();
      this.reader = new ConfigurationReader(this);
      this.executor = new ThreadPool(10);
      this.manager = new ProjectManager(reader, source, this, mode);
      this.logFile = logFile;
      this.level = level;
      this.path = path;
      this.root = root;
      
      createWorkspace();
   }
   
   public ClassPath getClassPath() {
      return path;
   }
   
   public File getRoot() {
      try {
         return root.getCanonicalFile();
      }catch(Exception e){
         throw new IllegalStateException("Could not determine workspace root", e);
      }
   }
   
   public Decompiler getDecompiler() {
      try {
         File outputDir = HomeDirectory.getPath(WorkspaceConfiguration.TEMP_PATH);
         return new Decompiler(outputDir);
      }catch(Exception e){
         throw new IllegalStateException("Could not determine workspace root", e);
      }
   }
   
   public Executor getExecutor(){
      return executor;
   }
   
   @Override
   public Project getByName(String name){ 
      return manager.getProject(name);
   }
   
   @Override
   public Project getByPath(Path path){ // /project/<project-name>/ || /project/default
      return manager.getProject(path);
   }
   
   public Project createProject(String name){ 
      return manager.createProject(name);
   }
   
   public Project createProject(Path path){ // /project/<project-name>/ || /project/default
      return manager.createProject(path);
   }
   
   public Map<String, String> getEnvironmentVariables() {
      try {
         return reader.loadWorkspaceConfiguration().getEnvironmentVariables();
      } catch(Exception e) {
         throw new IllegalStateException("Could not resolve environment variables", e);
      }  
   }
   
   public List<String> getArguments() {
      try {
         return reader.loadWorkspaceConfiguration().getArguments();
      } catch(Exception e) {
         throw new IllegalStateException("Could not resolve arguments", e);
      }  
   }
   
   public List<DependencyFile> resolveDependencies(List<Dependency> dependencies) {
      return reader.loadWorkspaceConfiguration().getDependencies(dependencies);
   }
   
   public File createFile(String name) {
      File file = new File(root, name);
      
      try {
         File directory = file.getParentFile();
         
         if(!directory.exists()) {
            directory.mkdirs();
         }
         return file.getCanonicalFile();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + file, e);
      }
   }
   
   public File createWorkspace() {
      try {
         WorkspaceLogger.create(logFile, level);
         return newWorkspace();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + root, e);
      }
   }

   private File newWorkspace() throws IOException {
      File directory = root.getCanonicalFile();
      File workspaceFile = new File(directory, WORKSPACE_FILE);
      
      if(!directory.exists() || !workspaceFile.exists()){
         directory.mkdirs();
         createDefaultWorkspace(directory);
      }
      getProjects();// resolve the dependencies
      return directory;
   }

   
   public List<Project> getProjects() {
      try {
         File workspace = root.getCanonicalFile();
         
         if(workspace.exists()) {
            File[] directories = workspace.listFiles();
            
            if(directories != null) {
               for(File directory : directories) {
                  String name = directory.getName();
                 
                  if(directory.isDirectory() && !name.startsWith(".")) {
                     File file = new File(directory, ProjectConfiguration.PROJECT_FILE);
                     
                     if(file.exists()) {
                        final Project project = createProject(name);
                        
                        if(project != null && loaded.add(name)) {
                           getExecutor().execute(new Runnable() {
                              @Override
                              public void run() {
                                 try {
                                    String name = project.getName();
   
                                    log.info("Loading project {}", name);
                                    ProgressManager.getProgress().update("Loading project " + name);
                                    project.getClassPath(); // resolve dependencies
                                    project.getIndexDatabase().getTypeNodes(); // index all classes
                                 }catch(Throwable e) {}
                              }
                           });
                        }
                     }
                  }
               }
            }
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not get projects in directory " + root, e);
      }
      return loaded.stream()
            .filter(Objects::nonNull)
            .sorted()
            .map(this::getByName)
            .filter(Objects::nonNull)
            .filter(Project::isValidProject)
            .collect(Collectors.toList());
   }
   
   private void createDefaultWorkspace(File file) {
      try {
         File directory = file.getCanonicalFile();
         
         if(!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Could not build project directory " + directory);
         }
         createDefaultFile(directory, ".gitignore", "/.display\n/.workspace\n/.temp/\n/.backup/\n");
         createDefaultFile(directory, ".workspace", "<workspace></workspace>\n");
      }catch(Exception e) {
         log.info("Could not create default workspace at '" + file + "'", e);
      }
   }
   
   public static void createDefaultFile(File file, String name, String content) throws Exception {
      File directory = file.getCanonicalFile();
      
      if(!directory.exists() && !directory.mkdirs()) {
         throw new IllegalStateException("Could not build project directory " + directory);
      }
      File ignore = new File(directory, name);
      FileWriter stream = new FileWriter(ignore);
      
      stream.write(content);
      stream.close();
   }
}