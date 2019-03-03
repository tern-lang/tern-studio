package org.ternlang.studio.index.classpath;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ternlang.studio.index.IndexNode;

public class ClassPathSearcher {
   
   private final Map<String, IndexNode> cache;
   private final List<IndexNode> nodes;
   
   public ClassPathSearcher(List<File> path) {
      this.cache = new ConcurrentHashMap<String, IndexNode>();
      this.nodes = ProjectClassPath.getProjectClassPath(path);
   }
   
   public Map<String, IndexNode> getTypeNodesMatching(String expression) {
      Map<String, IndexNode> matches = new HashMap<String, IndexNode>();
      
      for(IndexNode node : nodes) {
         String fullName = node.getFullName();
         String name = node.getName();

         if(!name.isEmpty() && name.matches(expression)) {
            matches.put(fullName, node);
         }
      }
      return matches;
   }
   
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      if(cache.isEmpty()) {
         for(IndexNode node : nodes) {
            String fullName = node.getFullName();
            
            if(!fullName.isEmpty()) {
               cache.put(fullName, node);
            }
         }
      }
      return Collections.unmodifiableMap(cache);
   }
   
   public IndexNode getTypeNode(String fullName) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      IndexNode node = nodes.get(fullName);
      
      if(node == null) {
         node = SystemClassPath.getDefaultNodesByType().get(fullName);
      }
      return node;
   }
}
