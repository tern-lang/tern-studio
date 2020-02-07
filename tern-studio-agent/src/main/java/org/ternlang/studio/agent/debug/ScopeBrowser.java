package org.ternlang.studio.agent.debug;

import org.ternlang.agent.message.common.VariablePathArray;

public interface ScopeBrowser {
   void browse(VariablePathArray expand);
   void evaluate(VariablePathArray expand, String expression);
   void evaluate(VariablePathArray expand, String expression, boolean refresh);
}