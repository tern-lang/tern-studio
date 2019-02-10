package tern.studio.agent.debug;

import java.util.List;

import tern.studio.agent.debug.ScopeNode;

public interface ScopeNode {
   int getDepth();
   String getName();
   String getAlias();
   String getPath();
   List<ScopeNode> getNodes();
}