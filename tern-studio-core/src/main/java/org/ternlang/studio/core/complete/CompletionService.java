package org.ternlang.studio.core.complete;

import org.ternlang.service.annotation.Component;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.complete.CompletionCompiler;
import org.ternlang.studio.index.complete.FindConstructorsInScope;
import org.ternlang.studio.index.complete.FindForExpression;
import org.ternlang.studio.index.complete.FindInScopeMatching;
import org.ternlang.studio.index.complete.FindMethodReference;
import org.ternlang.studio.index.complete.FindPossibleImports;
import org.ternlang.studio.index.complete.FindTraitToImplement;
import org.ternlang.studio.index.complete.FindTypesToExtend;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CompletionService {

   private final Workspace workspace;

   public CompletionCompiler create(String name) {
      Project project = workspace.getByName(name);
      ClassLoader classLoader = project.getClassLoader();
      Thread thread = Thread.currentThread();
      thread.setContextClassLoader(classLoader);
      IndexDatabase database = project.getIndexDatabase();
      
      return new CompletionCompiler(database,
            FindConstructorsInScope.class,
            FindPossibleImports.class,
            FindTypesToExtend.class,
            FindTraitToImplement.class,
            FindForExpression.class,
            FindInScopeMatching.class,
            FindMethodReference.class);
   }
}
