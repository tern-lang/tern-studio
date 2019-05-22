package org.ternlang.studio.core.tree;

import java.io.File;
import java.io.IOException;

import org.simpleframework.module.annotation.Component;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;
import org.ternlang.studio.project.Workspace;

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