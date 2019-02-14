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

public class FindConstructorsInScope implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile(".*new\\s+([a-zA-Z0-9_]*)$");
   
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
      Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
      Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
      String unfinished = text.getUnfinished();
      
      if(!entries.isEmpty()) {
         Set<IndexNode> matched = new HashSet<IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            IndexNode value = entry.getValue();
            IndexType type = value.getType();
            
            if(name.startsWith(unfinished)) {
               if(type.isImport() || type.isClass()) {
                  String fullName = value.getFullName();
                  IndexNode imported = database.getTypeNode(fullName);
                  
                  if(imported != null) {
                     Set<IndexNode> nodes = imported.getNodes();
                     
                     for(IndexNode child : nodes) {
                        IndexType childType = child.getType();
                        
                        if(childType.isConstructor()) {
                           matched.add(child);
                        }
                     }
                  }
               } else if(type.isConstructor()) {
                  matched.add(value);
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
}
