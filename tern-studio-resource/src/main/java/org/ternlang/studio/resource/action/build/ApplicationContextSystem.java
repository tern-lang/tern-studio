package org.ternlang.studio.resource.action.build;

import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeanOfType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

public class ApplicationContextSystem implements DependencySystem {
   
   private final ApplicationContext context;
   
   public ApplicationContextSystem(ApplicationContext context) {
      this.context = context;
   }

   @Override
   public <T> T resolve(Class<T> type) {
      return context.getBean(type);
   }
   
   @Override
   public <T> List<T> resolveAll(Class<T> type) {
      return context.getBeansOfType(type)
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
   }

   @Override
   public <T> T resolve(Class<T> type, String name) {
      return qualifiedBeanOfType(context, type, name);
   }

   @Override
   public void register(Object value) {
   }
}
