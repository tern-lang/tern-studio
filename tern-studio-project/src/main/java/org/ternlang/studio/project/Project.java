package org.ternlang.studio.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executor;

import org.ternlang.common.store.NotFoundException;
import org.ternlang.common.store.Store;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.studio.common.DirectoryWatcher;
import org.ternlang.studio.common.FileDirectory;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.project.config.ClassPathExecutor;
import org.ternlang.studio.project.config.ConfigurationClassLoader;
import org.ternlang.studio.project.config.ConfigurationReader;
import org.ternlang.studio.project.config.DependencyFile;
import org.ternlang.studio.project.generate.ConfigFileSource;

public class Project implements FileDirectory {
   
   private final ConfigurationClassLoader classLoader;
   private final ArchiveBuilder archiveBuilder;
   private final FileSystem fileSystem;
   private final ProjectContext context;
   private final Workspace workspace;
   private final String projectName;
   private final String projectDirectory;
   private final Store store;

   public Project(ConfigurationReader reader, ConfigFileSource source, Workspace workspace, String projectDirectory, String projectName) {
      this.context = new ProjectContext(reader, source, workspace, this);
      this.classLoader = new ConfigurationClassLoader(this);
      this.archiveBuilder = new ArchiveBuilder(this, context);
      this.fileSystem = new FileSystem(this);
      this.store = new ProjectStore();
      this.projectDirectory = projectDirectory;
      this.projectName = projectName;
      this.workspace = workspace;
   }
   
   public File getExportedArchive(String mainScript) {
      return archiveBuilder.exportArchive(mainScript);
   }
   
   public Workspace getWorkspace(){
      return workspace;
   }
   
   public String getProjectDirectory() {
      return projectDirectory;
   }

   public String getName() {
      return projectName;
   }

   public FileSystem getFileSystem() {
      return fileSystem;
   }
   
   public ClassLoader getClassLoader() {
      return classLoader.getClassLoader();
   }
   
   public long getModificationTime(){
      return DirectoryWatcher.lastModified(getBasePath());
   }

   public File getBasePath() {
      try {
         return workspace.createFile(projectName);
      } catch (Exception e) {
         throw new IllegalStateException("Could not get project path for '" + projectName + "'", e);
      }
   }
   
   public Context getProjectContext() {
      Executor threadPool = workspace.getExecutor();
      try {
         ClassPathExecutor executor = new ClassPathExecutor(this, threadPool);
         return new StoreContext(store, executor);
      }catch(Exception e) {
         throw new IllegalStateException("Could not create context for '" + projectName + "'", e);
      }
   }
   
   public IndexDatabase getIndexDatabase(){
      return context.getIndexDatabase();
   }
   
   public boolean isLayoutPath(String resource) {
      return context.getLayout().isLayoutPath(resource);
   }
   
   public String getRealPath(String resource) {
      File path = getBasePath();
      return context.getLayout().getRealPath(path, resource);
   }
   
   public String getScriptPath(String resource) {
      File path = getBasePath();
      return context.getLayout().getDownloadPath(path, resource);
   }
    
   public List<DependencyFile> getDependencies() {
      return context.getDependencies();
   }

   public List<DependencyFile> getDependencies(boolean refresh) {
      return context.getDependencies(refresh);
   }
   
   public ClassPathFile getClassPath() {
      return context.getClassPath();
   }

   @Override
   public String toString(){
      return projectName;
   }
   
   private class ProjectStore implements Store {
   
      @Override
      public InputStream getInputStream(String path) {
         try {
            ProjectLayout layout = context.getLayout();
            File rootPath = getBasePath();
            String projectPath = layout.getRealPath(rootPath, path);
            File realFile = fileSystem.getFile(projectPath);
            return new FileInputStream(realFile);
         } catch(Exception e) {
            throw new NotFoundException("Could not get source path for '" + path + "'", e);
         }
      }
   
      @Override
      public OutputStream getOutputStream(String path) {
         return System.out;
      }
   }
}