package org.ternlang.studio.resource.action.build;

import java.util.List;

public interface DependencySystem {
   <T> T resolve(Class<T> type);
   <T> T resolve(Class<T> type, String name);
   <T> List<T> resolveAll(Class<T> type);
   void register(Object value);
}
