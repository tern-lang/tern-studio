package org.ternlang.studio.core.tree;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.config.ProjectConfiguration;

public class TreeContext implements TreeFolderExpander {
   
   private final Workspace workspace;
   private final Set<String> expand;
   private final String projectName;
   private final File root;
   private final boolean isProject;
   
   public TreeContext(Workspace workspace, File root, String projectName, boolean isProject) {
      this.expand = new CopyOnWriteArraySet<String>();
      this.workspace = workspace;
      this.isProject = isProject;
      this.projectName = projectName;
      this.root = root;
   }
   
   public boolean isProject() {
      return isProject;
   }
   
   public File getRoot() {
      return root;
   }
   
   public String getProject() {
      return projectName;
   }
   
   public boolean isVisiblePath(String path) {
      if(!isProject) {
         File projectPath = new File(root, getRelativePath(path));
         if(projectPath.exists() && projectPath.isDirectory()) {
            File projectConfig = new File(projectPath, ProjectConfiguration.PROJECT_FILE);
            return projectConfig.exists() && projectConfig.isFile();
         }
         return false;
      }
      return true;
   }
   
   public boolean isLayoutPath(String prefix, String path) {
      if(isProject) {
         Project project = workspace.getByName(projectName);
         
         if(project != null) {
            path = getRelativePath(path);
            return project.isLayoutPath(path);
         }
      }
      return false;
   }
   
   private String getRelativePath(String path) {
      String resourcePrefix = "/resource/" + projectName;
      
      if(path.startsWith(resourcePrefix)) {
         int length = resourcePrefix.length();
         return path.substring(length);
      }
      return path;
   }
   
   public Set<String> getExpandFolders() {
      return Collections.unmodifiableSet(expand);
   }
   
   public TreeContext folderExpand(String path) {
      String result = TreePathFormatter.formatPath(projectName, path);
      expand.add(result);
      return this;
   }
   
   public TreeContext folderCollapse(String path) {
      String result = TreePathFormatter.formatPath(projectName, path);
      expand.remove(result);
      return this;
   }

   @Override
   public boolean expand(String path) {
      return path != null && expand.contains(path);
   }
   
}