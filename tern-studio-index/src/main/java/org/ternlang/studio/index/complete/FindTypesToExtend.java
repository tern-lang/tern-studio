package org.ternlang.studio.index.complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class FindTypesToExtend implements CompletionFinder {
   
   private static final Pattern PATTERN = Pattern.compile(".*\\s*extends\\s+([a-zA-Z0-9_]*)$");
   
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
      Set<IndexNode> types = findAllTypes(database, node, unfinished);
      
      if(!types.isEmpty()) {
         Set<IndexNode> result = new HashSet<IndexNode>();
         
         for(IndexNode entry : types) {
            IndexType index = entry.getType();
            
            if(index.isClass()) {
               result.add(entry);
            }
         }
         return result;
      }
      return Collections.emptySet();
   }
   
   public static Set<IndexNode> findAllTypes(IndexDatabase database, IndexNode node, String unfinished) throws Exception {
      Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
      Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
      
      if(!entries.isEmpty()) {
         Set<IndexNode> matched = new HashSet<IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            
            if(name.startsWith(unfinished)) {
               IndexNode value = entry.getValue();
               IndexType type = value.getType();
               
               if(type.isImport()) {
                  String fullName = value.getFullName();
                  
                  value = database.getTypeNode(fullName);
                  type = value.getType();
               }
               if(type.isType()) {
                  matched.add(value);
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
}
