package org.ternlang.studio.index.complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class FindTraitToImplement implements CompletionFinder {
   
   private static final Pattern PATTERN = Pattern.compile(".*\\s*with\\s+([a-zA-Z0-9_]*)$");
   
   @Override
   public InputExpression parseExpression(EditContext context) {
      String expression = context.getOriginalExpression();
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String unfinished = matcher.group(1);
         return new InputExpression(null, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception {
      String unfinished = text.getUnfinished();
      Set<IndexNode> types = FindTypesToExtend.findAllTypes(database, node, unfinished);
      
      if(!types.isEmpty()) {
         Set<IndexNode> result = new HashSet<IndexNode>();
         
         for(IndexNode entry : types) {
            IndexType index = entry.getType();
            
            if(index.isTrait()) {
               result.add(entry);
            }
         }
         return result;
      }
      return Collections.emptySet();
   }
}
