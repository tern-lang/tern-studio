package org.ternlang.studio.resource.server;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.ternlang.core.Any;
import org.ternlang.core.ContextClassLoader;
import org.ternlang.studio.resource.boot.PathMatchingClassScanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.ternlang.studio.resource.action.annotation.Component
public class ResourceClassScanner {
   
   private static final String RESOURCE_PACKAGE = "org.ternlang.";
   
   private final PathMatchingClassScanner resolver;
   
   public ResourceClassScanner() {
      this.resolver = new PathMatchingClassScanner(); 
   }

   public Set<Class> scan(Class<? extends Annotation> annotation) {
      try {
         return resolver.findClasses(RESOURCE_PACKAGE, annotation);
      } catch(Exception e) {
         throw new IllegalStateException("Could not scan for resources", e);
      }
   }
}
