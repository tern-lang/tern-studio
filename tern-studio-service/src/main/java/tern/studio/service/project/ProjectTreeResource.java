package tern.studio.service.project;

import static tern.studio.common.resource.SessionConstants.SESSION_ID;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.studio.common.resource.Resource;
import tern.studio.common.resource.ResourcePath;
import tern.studio.common.resource.display.DisplayModelResolver;
import tern.studio.project.Project;
import tern.studio.project.Workspace;
import tern.studio.service.tree.TreeBuilder;
import tern.studio.service.tree.TreeContext;
import tern.studio.service.tree.TreeContextManager;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/tree.*")
public class ProjectTreeResource implements Resource {
   
   private final TreeContextManager contextManager;
   private final AtomicInteger sessionCounter;
   private final TreeBuilder treeBuilder;
   private final Workspace workspace;

   public ProjectTreeResource(Workspace workspace, TreeContextManager contextManager, DisplayModelResolver modelResolver) {
      this.treeBuilder = new TreeBuilder(modelResolver);
      this.sessionCounter = new AtomicInteger();
      this.contextManager = contextManager;
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String name = request.getParameter("id");
      String expand = request.getParameter("expand");
      String folders = request.getParameter("folders");
      String depth = request.getParameter("depth");
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
      int count = sessionCounter.getAndIncrement();
      Cookie cookie = request.getCookie(SESSION_ID);
      String value = String.valueOf(count);
      
      if(cookie != null) {
         value = cookie.getValue();
      } else {
         response.setCookie(SESSION_ID, value);
      }
      String projectName = treePath.getName();
      TreeContext context = contextManager.getContext(treePath, projectName, value, isProject);

      context.folderExpand(expand);
      String result = treeBuilder.createTree(context, name, foldersOnly, folderDepth);
      PrintStream out = response.getPrintStream();
      response.setContentType("text/html");
      out.println(result);
      out.close();
   }
}