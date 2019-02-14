package org.ternlang.studio.index.complete;

import java.util.Set;

import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.expression.ExpressionFinder;

public class FindForExpression implements CompletionFinder {

   @Override
   public InputExpression parseExpression(EditContext context) {
      String expression = context.getExpression();
      
      if(expression.contains(".")) {
         String text = expression.trim();
         return new InputExpression(text, null);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception {
      ExpressionFinder finder = new ExpressionFinder(database);
      String expression = text.getHandle();
      return finder.find(node, expression);
   }

}
