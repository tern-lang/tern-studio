package org.ternlang.studio.index;

import java.util.Map;

public interface IndexDatabase {
   IndexNode getDefaultImport(String module, String name) throws Exception;
   IndexNode getTypeNode(String type) throws Exception;
   Map<String, IndexNode> getTypeNodesMatching(String regex) throws Exception;
   Map<String, IndexNode> getTypeNodesMatching(String regex, boolean ignoreCase) throws Exception;
   Map<String, IndexNode> getTypeNodes() throws Exception;
   Map<String, SourceFile> getFiles() throws Exception;
   Map<String, IndexNode> getImportsInScope(IndexNode node) throws Exception;
   Map<String, IndexNode> getNodesInScope(IndexNode node) throws Exception;
   Map<String, IndexNode> getMemberNodes(IndexNode node) throws Exception;
   SourceFile getFile(String resource, String source) throws Exception;
}
