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

public class FindInScopeMatching implements CompletionFinder {

   private static final Pattern[] PATTERNS = {
      Pattern.compile("\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*return\\s+([a-zA-Z0-9_]*)$"),
      Pattern.compile(".*\\s*->\\s+([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*var\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*let\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*const\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile(".*\\s+var\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile(".*\\s+let\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile(".*\\s+const\\s+[a-zA-Z0-9_]+\\s*\\:\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*var\\s+[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*let\\s+[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*const\\s+[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*var\\s+[a-zA-Z0-9_]+\\s*\\:\\s*[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*let\\s+[a-zA-Z0-9_]+\\s*\\:\\s*[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
      Pattern.compile("\\s*const\\s+[a-zA-Z0-9_]+\\s*\\:\\s*[a-zA-Z0-9_]+\\s*=\\s*([a-zA-Z0-9_]*)$"),
   };
   
   @Override
   public InputExpression parseExpression(EditContext context) {
      String expression = context.getOriginalExpression();
      
      for(Pattern pattern : PATTERNS) {
         Matcher matcher = pattern.matcher(expression);
         
         if(matcher.matches()) {
            String unfinished = matcher.group(1);
            return new InputExpression(null, unfinished);
         }
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
            
            if(name.startsWith(unfinished)) {
               IndexNode value = entry.getValue();
               IndexType type = value.getType();
               
               if(type.isImport()) {
                  String fullName = value.getFullName();
                  IndexNode match = database.getTypeNode(fullName);
                  
                  if(match != null) {
                     type = match.getType();
                     value = match;
                  }
               }
               if(type.isType() || type.isConstrained()) {
                  matched.add(value);
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
}
