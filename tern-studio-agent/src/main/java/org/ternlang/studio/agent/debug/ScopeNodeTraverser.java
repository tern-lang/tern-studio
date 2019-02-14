package org.ternlang.studio.agent.debug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ternlang.core.Context;
import org.ternlang.core.scope.Scope;
import org.ternlang.studio.agent.debug.ScopeNode;
import org.ternlang.studio.agent.debug.ScopeNodeBuilder;
import org.ternlang.studio.agent.debug.ScopeNodeTree;

public class ScopeNodeTraverser {
   
   private final Context context;
   private final Scope scope;
   
   public ScopeNodeTraverser(Context context, Scope scope) {
      this.context = context;
      this.scope = scope;
   }
   
   public Map<String, Map<String, String>> expand(Set<String> expand) {
      Map<String, Map<String, String>> variables = new HashMap<String, Map<String, String>>();
      ScopeNodeBuilder builder = new ScopeNodeBuilder(variables, context);
      ScopeNode node = new ScopeNodeTree(builder, scope);
      
      if(!expand.isEmpty()) {
         for(String path : expand) {
            String[] parts = path.split("\\.");
            expand(node, parts, 0);
         }
      } else {
         node.getNodes(); // expand root
      }
      return variables;
   }

   private void expand(ScopeNode node, String[] parts, int index) {
      List<ScopeNode> children = node.getNodes();
      String match = parts[index];
      
      for(ScopeNode child : children) {
         String name = child.getAlias();
         
         if(name.equals(match)) {
            expand(child, parts, index+1);
            break;
         }
      }
   }
}