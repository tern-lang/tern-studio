package org.ternlang.studio.agent.debug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ternlang.core.Context;
import org.ternlang.core.function.Function;
import org.ternlang.core.scope.Scope;
import org.ternlang.studio.agent.debug.ExpressionProcessor;
import org.ternlang.studio.agent.debug.ExpressionScopeNode;
import org.ternlang.studio.agent.debug.ScopeNode;
import org.ternlang.studio.agent.debug.ScopeNodeBuilder;
import org.ternlang.studio.agent.debug.VariableNameEncoder;

public class ScopeNodeEvaluator {

   private final ExpressionProcessor processor; 
   private final VariableNameEncoder encoder;
   private final Context context;
   
   public ScopeNodeEvaluator(Context context, Scope scope, Function function) {
      this.processor = new ExpressionProcessor(context, scope, function); // this keeps expression cache
      this.encoder = new VariableNameEncoder();
      this.context = context;
   }
   
   public Map<String, Map<String, String>> expand(Set<String> expand, String expression, boolean refresh) {
      Map<String, Map<String, String>> variables = new HashMap<String, Map<String, String>>();
      ScopeNodeBuilder builder = new ScopeNodeBuilder(variables, context);
      ScopeNode node = new ExpressionScopeNode(builder, processor, encoder, expression, refresh);
      
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
      String match = encoder.decode(parts[index]);
      
      for(ScopeNode child : children) {
         String name = child.getName();
         
         if(name.equals(match)) {
            expand(child, parts, index+1);
            break;
         }
      }
   }
}