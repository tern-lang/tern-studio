package org.ternlang.studio.core.tree;

import java.io.File;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.common.SessionCookie;
import org.ternlang.studio.common.display.DisplayModelResolver;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import lombok.SneakyThrows;

@Component
public class TreeService {
   
   private final TreeContextManager contextManager;
   private final TreeBuilder treeBuilder;
   private final Workspace workspace;

   public TreeService(Workspace workspace, TreeContextManager contextManager, DisplayModelResolver modelResolver) {
      this.treeBuilder = new TreeBuilder(modelResolver);
      this.contextManager = contextManager;
      this.workspace = workspace;
   }

   @SneakyThrows
   public void tree(String name, String expand, String folders, String depth, Request request, Response response) {
      String value = SessionCookie.findOrCreate(request, response);
      Path path = request.getPath(); // /tree/<project-name>
      String[] segments = path.getSegments();
      File treePath = workspace.getRoot();
      boolean foldersOnly = false;
      boolean isProject = false;
      int folderDepth = Integer.MAX_VALUE;
      
      if(segments.length > 1) {
         Project project = workspace.createProject(path);
         treePath = project.getBasePath();
         isProject = true;
      }
      if(depth != null) {
         folderDepth = Integer.parseInt(depth);
      }
      if(folders != null) {
         foldersOnly = Boolean.parseBoolean(folders);
      }
      String projectName = treePath.getName();
      TreeContext context = contextManager.getContext(treePath, projectName, value, isProject);

      context.folderExpand(expand);
      String result = treeBuilder.createTree(context, value, name, foldersOnly, folderDepth);
      PrintStream out = response.getPrintStream();
      response.setContentType("text/html");
      out.println(result);
      out.close();
   }
}