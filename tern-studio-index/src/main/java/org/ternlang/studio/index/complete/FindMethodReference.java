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

public class FindMethodReference implements CompletionFinder {

   private static final Pattern  PATTERN = Pattern.compile(".*?([a-zA-Z0-9_]+)::([a-zA-Z0-9_]*)$");
   
   @Override
   public InputExpression parseExpression(EditContext context) {
      String expression = context.getOriginalExpression();
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String handle = matcher.group(1);
         String unfinished = matcher.group(2);
         return new InputExpression(handle, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, InputExpression text) throws Exception {
      Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
      String unfinished = text.getUnfinished();
      String handle = text.getHandle();
      IndexNode match = expandedScope.get(handle);
      
      if(match != null) {
         IndexType type = match.getType();
         
         if(type.isType()) {
            return findMembersMatching(database, match, unfinished);
         }
      }
      return Collections.emptySet();
   }
   
   private Set<IndexNode> findMembersMatching(IndexDatabase database, IndexNode node, String unfinished) throws Exception {
      Map<String, IndexNode> members = database.getMemberNodes(node);
      Set<Entry<String, IndexNode>> entries = members.entrySet();
      
      if(!members.isEmpty()) {
         Set<IndexNode> matched = new HashSet<IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            
            if(name.startsWith(unfinished)) {
               IndexNode value = entry.getValue();
               IndexType type = value.getType();
               
               if(type.isMemberFunction()) {
                  matched.add(value);
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
}
