package tern.studio.service.project;

import java.io.File;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import tern.common.thread.ThreadPool;
import tern.studio.common.FileAction;
import tern.studio.common.FileProcessor;
import tern.studio.common.FileReader;
import tern.studio.common.Problem;
import tern.studio.common.ProblemFinder;
import tern.studio.project.Project;
import tern.studio.project.Workspace;

@Slf4j
public class ProjectProblemFinder {

   private final FileProcessor<Problem> processor;
   private final FileAction<Problem> action;
   private final Workspace workspace;
   
   public ProjectProblemFinder(Workspace workspace, ThreadPool pool) {
      this.action = new CompileAction(workspace);
      this.processor = new FileProcessor<Problem>(action, pool);
      this.workspace = workspace;
   }
   
   public Set<Problem> compileProject(Path path) throws Exception {
      Project project = workspace.createProject(path);
      String name = project.getName();
      File directory = project.getBasePath();
      String root = directory.getCanonicalPath();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      long start = System.currentTimeMillis();
      
      try {
         return processor.process(name, root + "/**.snap"); // build all resources
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         if(log.isTraceEnabled()) {
            log.trace("Took " + duration + " ms to compile project " + name);
         }
      }
   }
   
   private static class CompileAction implements FileAction<Problem> {
   
      private final ProblemFinder finder;
      private final Workspace workspace;
      
      public CompileAction(Workspace workspace) {
         this.finder = new ProblemFinder();
         this.workspace = workspace;
      }
      
      @Override
      public Problem execute(String reference, File file) throws Exception {
         Project project = workspace.getByName(reference);
         String name = project.getName();
         File root = project.getBasePath();
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         if(log.isTraceEnabled()) {
            log.trace("Compiling " + resourcePath + " in project " + reference);
         }
         return finder.parse(name, resourcePath, source);
      }
   }
}