package org.ternlang.studio.agent.debug;

import java.util.Collections;
import java.util.List;

import org.ternlang.studio.agent.debug.ExpressionProcessor;
import org.ternlang.studio.agent.debug.ScopeNode;
import org.ternlang.studio.agent.debug.ScopeNodeBuilder;
import org.ternlang.studio.agent.debug.VariableNameEncoder;

public class ExpressionScopeNode implements ScopeNode {
   
   private final ExpressionProcessor processor;
   private final VariableNameEncoder encoder;
   private final ScopeNodeBuilder builder;
   private final String expression;
   private final boolean refresh;
   
   public ExpressionScopeNode(ScopeNodeBuilder builder, ExpressionProcessor processor, VariableNameEncoder encoder, String expression, boolean refresh) {
      this.expression = expression;
      this.processor = processor;
      this.refresh = refresh;
      this.encoder = encoder;
      this.builder = builder;
   }
   
   @Override
   public int getDepth() {
      return 0;
   }
   
   @Override
   public String getName() {
      return "";
   }
   
   @Override
   public String getAlias() {
      return "";
   }  
   
   @Override
   public String getPath() {
      return "";
   }

   @Override
   public List<ScopeNode> getNodes() {
      Object object = processor.evaluate(expression, refresh);
      
      if(expression != null) {
         String token = expression.trim();
         int length = token.length();
         
         if(length > 0) { // make sure something is evaluated
            String path = encoder.encode(expression);
            ScopeNode node = builder.createNode(path, expression, expression, object, 0, 0);
         
            if(node != null) {
               return Collections.singletonList(node);
            }
         }
      }
      return Collections.emptyList();
   }
}