package org.ternlang.studio.project.config;

import static org.ternlang.studio.project.config.ProjectConfiguration.CLASSPATH_FILE;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.studio.agent.core.ClassPathUpdater;
import org.ternlang.studio.project.ClassPathFile;
import org.ternlang.studio.project.FileSystem;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.studio.project.Project;

public class ConfigurationClassLoader {
   
   private final AtomicReference<ClassLoader> reference;
   private final AtomicLong lastUpdate;
   private final Project project;
   
   public ConfigurationClassLoader(Project project) {
      this.reference = new AtomicReference<ClassLoader>();
      this.lastUpdate = new AtomicLong();
      this.project = project;
   }
   
   public ClassLoader getClassLoader() {
      String name = project.getName();
      long time = System.currentTimeMillis();
      
      try {
         if(isClassLoaderStale()) {
            ClassPathFile classPath = project.getClassPath();
            String content = classPath.getPath();
            ClassLoader classLoader = createClassLoader(content);
            
            lastUpdate.set(time);
            reference.set(classLoader);
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not create class loader for project '" +name +"'", e);
      }
      return reference.get();
   }
   
   private boolean isClassLoaderStale() {
      ClassLoader classLoader = reference.get();
   
      if(classLoader != null) {
         FileSystem fileSystem = project.getFileSystem();
         File classPathFile = fileSystem.getFile(CLASSPATH_FILE);
         long lastModified = classPathFile.lastModified();
         long updateTime = lastUpdate.get();
         
         return classPathFile.exists() && updateTime < lastModified;
      }
      return true;
   }
   
   private ClassLoader createClassLoader(String dependencies) {
      try {
         List<File> files = ClassPathUpdater.parseClassPath(dependencies);
         File tempPath = HomeDirectory.getPath(WorkspaceConfiguration.TEMP_PATH);
         File agentFile = new File(tempPath, WorkspaceConfiguration.JAR_FILE);
         List<URL> locations = new ArrayList<URL>();
         
         if(agentFile.exists()) {
            files.add(agentFile);
         }
         URL[] array = new URL[]{};
         
         for(File file : files) {
            URL location = file.toURI().toURL();
            locations.add(location);
         }
         return new URLClassLoader(
               locations.toArray(array),
               ClassLoader.getSystemClassLoader().getParent());
      } catch(Exception e) {
         throw new IllegalStateException("Could not create project class loader", e);
      }
   }
}