package tern.studio.service.tree;

import java.io.File;
import java.io.IOException;

import tern.common.Cache;
import tern.common.LeastRecentlyUsedCache;
import tern.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
public class TreeContextManager {

   private final Cache<String, TreeContext> contexts;
   private final Workspace workspace;

   public TreeContextManager(Workspace workspace) {
      this.contexts = new LeastRecentlyUsedCache<String, TreeContext>(1000);
      this.workspace = workspace;
   }
   
   public TreeContext getContext(File path, String project, String cookie, boolean isProject) throws IOException {
      String realPath = path.getCanonicalPath();
      File realFile = path.getCanonicalFile();
      String key = String.format("%s-%s-%s", realPath, project, cookie);
      TreeContext context = contexts.fetch(key);
            
      if(context == null) {
         context = new TreeContext(workspace, realFile, project, isProject);
         contexts.cache(key, context);
      }
      return context;
   }
}