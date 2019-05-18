package org.ternlang.studio.resource.action.build;

import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeanOfType;

import org.springframework.context.ApplicationContext;

public class ApplicationContextSystem implements DependencySystem {
   
   private final ApplicationContext context;
   
   public ApplicationContextSystem(ApplicationContext context) {
      this.context = context;
   }

   @Override
   public Object getDependency(Class type) {
      return context.getBean(type);
   }

   @Override
   public Object getDependency(Class type, String name) {
      return qualifiedBeanOfType(context, type, name);
   }
}
