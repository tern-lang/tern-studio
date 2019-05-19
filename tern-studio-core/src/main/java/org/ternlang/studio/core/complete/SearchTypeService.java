package org.ternlang.studio.core.complete;

import java.util.Map;

import org.ternlang.service.annotation.Component;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Component
@AllArgsConstructor
public class SearchTypeService {
   
   private final Workspace workspace;

   @SneakyThrows
   public Map<String, SearchTypeResult> search(String name, String expression) {
      String text = SearchExpressionParser.parse(expression);
      Project project = workspace.getByName(name);
      Thread thread = Thread.currentThread();
      ClassLoader classLoader = project.getClassLoader();
      thread.setContextClassLoader(classLoader);
      return SearchTypeCollector.search(project, text);
   }
}
