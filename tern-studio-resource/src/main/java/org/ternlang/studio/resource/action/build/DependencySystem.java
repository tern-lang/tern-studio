package org.ternlang.studio.resource.action.build;

public interface DependencySystem {
   Object getDependency(Class type);
   Object getDependency(Class type, String name);
}
