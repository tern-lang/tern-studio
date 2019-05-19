package org.ternlang.studio.resource.boot;

import java.util.Queue;

public interface DependencyScanner {
   Queue<Class> scan();
}
