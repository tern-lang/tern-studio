package org.ternlang.studio.resource.action.build;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ComponentFinder {

   private final Set<Class> types;

   public ComponentFinder(Class... types) {
      this(Arrays.asList(types));
   }

   public ComponentFinder(Collection<Class> types) {
      this.types = new LinkedHashSet<Class>(types);
   }

   public Set<Class> getComponents() {
      return types;
   }
}
