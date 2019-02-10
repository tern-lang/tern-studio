package tern.studio.service.project;

import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.LAST_MODIFIED;
import static org.simpleframework.http.Protocol.PRAGMA;

import java.io.OutputStream;
import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import tern.core.Reserved;
import tern.studio.common.resource.ContentTypeResolver;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.project.FileCache;
import tern.studio.project.FileData;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import tern.studio.project.decompile.Decompiler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ResourcePath("/resource/.*")
public class ProjectFileResource implements Resource {
   
   private final ContentTypeResolver resolver;
   private final FileCache cache;
   private final Workspace workspace;

   public ProjectFileResource(Workspace workspace, ContentTypeResolver resolver){
      this.cache = new FileCache(workspace);
      this.workspace = workspace;
      this.resolver = resolver;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      Project project = workspace.getByPath(path);
      String projectPath = getPath(project, request); // /<project-name>/<project-path> or /default/blah.snap
   
      if(projectPath.startsWith("/decompile") && projectPath.endsWith(".java")) {
         handleDecompile(request, response);
      } else {
         handleFile(request, response);
      }
   }
   
   private void handleFile(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      Project project = workspace.getByPath(path);
      String projectName = project.getName();
      String projectPath = getPath(project, request); // /<project-name>/<project-path> or /default/blah.snap
      FileData projectFile = cache.getFile(projectName, projectPath);
      OutputStream stream = response.getOutputStream();
      String type = resolver.resolveType(projectPath);
      String method = request.getMethod();
      
      response.setStatus(Status.OK);
      response.setContentType(type);

      if(log.isTraceEnabled()) {
         log.trace(method + ": " + path);
      }
      try {
         byte[] resource = projectFile.getByteArray();
         long lastModified = projectFile.getLastModified();

         response.setValue(CACHE_CONTROL, "no-cache, max-age=0, must-revalidate, proxy-revalidate");
         response.setValue(PRAGMA, "no-cache");
         response.setDate(LAST_MODIFIED, lastModified);
         stream.write(resource);
         stream.close();
      }catch(Exception e) {
         PrintStream out = response.getPrintStream();
         response.setStatus(Status.NOT_FOUND);
         
         if(projectPath.endsWith(Reserved.SCRIPT_EXTENSION)){
            out.println("// No source found for " + projectPath);
         }
         out.close();
      }
   }
   
   // /resource/<project-name>/decompile/<jar-file>/<package-name>/<name>.java
   private void handleDecompile(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String[] segments = path.getSegments();
      String className = path.getPath(segments.length -2); // remove leading slash
      String jarFile = path.getPath(3, segments.length-5);
      String method = request.getMethod();
      
      if(className.startsWith("/")) {
         className = className.substring(1);
      }
      if(className.contains("/")) {
         className = className.replace("/", ".");
      }
      if(className.endsWith(".java")) {
         int length = className.length();
         int chop = ".java".length();
         
         className = className.substring(0, length-chop);
      }
      if(jarFile.startsWith("/")) {
         jarFile = jarFile.substring(1);
      }
      Decompiler decompiler = workspace.getDecompiler();
      String source = decompiler.decompile(jarFile, className);
      PrintStream stream = response.getPrintStream();

      if(log.isTraceEnabled()) {
         log.trace(method + ": " + path);
      }
      response.setContentType("text/plain");
      stream.print(source);
      stream.close();
   }
   
   protected String getPath(Project project, Request request) throws Exception {
      Path path = request.getPath(); 
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      
      if(isDownload()) {
         return project.getRealPath(projectPath);
      }
      return projectPath;
   }
   
   protected boolean isDownload(){
      return false;
   }
}