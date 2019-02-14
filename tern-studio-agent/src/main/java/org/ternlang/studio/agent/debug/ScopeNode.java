package org.ternlang.studio.agent.debug;

import java.util.List;

import org.ternlang.studio.agent.debug.ScopeNode;

public interface ScopeNode {
   int getDepth();
   String getName();
   String getAlias();
   String getPath();
   List<ScopeNode> getNodes();
}