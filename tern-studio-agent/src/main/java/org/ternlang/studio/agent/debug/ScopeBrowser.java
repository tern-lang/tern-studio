package org.ternlang.studio.agent.debug;

import java.util.Set;

public interface ScopeBrowser {
   void browse(Set<String> expand);
   void evaluate(Set<String> expand, String expression);
   void evaluate(Set<String> expand, String expression, boolean refresh);
}