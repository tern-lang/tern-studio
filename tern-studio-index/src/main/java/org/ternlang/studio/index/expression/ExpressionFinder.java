package org.ternlang.studio.index.expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.core.Bug;
import org.ternlang.core.Reserved;
import org.ternlang.core.type.Type;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

@Slf4j
public class ExpressionFinder {
   
   private final ExpressionExtractor extractor;
   private final IndexDatabase database;
   
   public ExpressionFinder(IndexDatabase database) {
      this.extractor = new ExpressionExtractor();
      this.database = database;
   }
   
   public Set<IndexNode> find(IndexNode node, String text) {
      Expression expression = extractor.extract(text);
      return collect(node, expression);
   }

   private Set<IndexNode> collect(IndexNode node, Expression expression) {
      List<ExpressionToken> tokens = expression.getTokens();
      int count = tokens.size();
      
      for(int i = 0; i < count -1; i++) {
         ExpressionToken token = tokens.get(i);
         IndexNode next = findMatch(node, token);
         
         if(next == null) {
            return Collections.emptySet();
         }
         node = next;
      }
      ExpressionToken last = tokens.get(count-1);
      return expand(node, last);
   }
   
   private Set<IndexNode> expand(IndexNode node, ExpressionToken token) {
      IndexType type = node.getType();
      String unfinished = token.getName();
      
      if(type.isType()) {
         return expandMembers(node, unfinished);
      }
      return expandInScope(node, unfinished);
   }
   
   private Set<IndexNode> expandMembers(IndexNode node, String unfinished) {
      try {
         Map<String, IndexNode> members = database.getMemberNodes(node);
         return filterExpandedMatches(members, unfinished);
      } catch(Throwable e) {
         log.info("Could not expand members", e);
      }
      return Collections.emptySet();
   }
   
   private Set<IndexNode> expandInScope(IndexNode node, String unfinished) {
      try {
         Map<String, IndexNode> members = database.getNodesInScope(node);
         return filterExpandedMatches(members, unfinished);
      } catch(Throwable e) {
         log.info("Could not expand scope", e);
      }
      return Collections.emptySet();
   }
   
   private Set<IndexNode> filterExpandedMatches(Map<String, IndexNode> matches, String unfinished) {      
      Set<IndexNode> matched = new HashSet<IndexNode>();
      
      try {
         Set<Entry<String, IndexNode>> entries = matches.entrySet();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            IndexNode childNode = entry.getValue();
            IndexType type = childNode.getType();
            
            if(name.startsWith(unfinished)) {
               if(type.isProperty() || type.isMemberFunction()) {
                  matched.add(childNode);
               }
            }
         }
      } catch(Exception e) {
         log.info("Could not expand matches", e);
      }
      return matched;
   }
   
   private IndexNode findMatch(IndexNode node, ExpressionToken token) {
      ExpressionBraceType braces = token.getBraces();
      List<ExpressionArgument> arguments = token.getArguments();
      
      if(arguments.isEmpty()) {
         if(braces == ExpressionBraceType.INVOKE) {
            return findFunction(node, token);
         }
         return findVariableOrType(node, token);
      }
      if(braces == ExpressionBraceType.INDEX) {
         return findVariableOrType(node, token);
      }
      return findFunction(node, token);
   }
   
   private IndexNode findVariableOrType(IndexNode node, ExpressionToken token) {
      IndexNode match = findVariable(node, token);
      
      if(match == null) {
         try {
            String name = token.getName();
            Map<String, IndexNode> types = database.getNodesInScope(node);
            
            return types.get(name);
         } catch(Exception e) {
            log.info("Could not find nodes", e);
         }
      }
      return match;
   }

   @Bug("fix this")
   private IndexNode findVariable(IndexNode node, ExpressionToken token) {
      String name = token.getName();
      
      if(name.equals(Reserved.TYPE_THIS)) {
         return findEnclosingThis(node);
      }
      if(name.equals(Reserved.TYPE_SUPER)) {
         return findEnclosingSuper(node);
      }
      if(name.equals(Reserved.TYPE_CLASS)) {
         throw new RuntimeException();
         //   return ClassIndexProcessor.getIndexNode(Type.class);
      }
      IndexType type = node.getType();
      
      if(type.isType()) {
         return findMemberProperty(node, name);
      }
      return findVariableInScope(node, name);
   }
   
   private IndexNode findMemberProperty(IndexNode node, String name) {
      try {
         Map<String, IndexNode> expandedScope = database.getMemberNodes(node);
         IndexNode match = expandedScope.get(name);
         
         if(match != null) {
            return findConstraint(match);
         }
      } catch(Throwable e) {
         log.info("Could not find property " + name, e);
      }
      return null;
   }
   
   private IndexNode findVariableInScope(IndexNode node, String name) {
      try {
         Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
         IndexNode match = expandedScope.get(name);
         
         if(match != null) {
            return findConstraint(match);
         }
      } catch(Throwable e) {
         log.info("Could not find variable " + name, e);
      }
      return null;
   }
   
   private IndexNode findFunction(IndexNode node, ExpressionToken token) {
      Pattern pattern = createFunctionPattern(token);
      IndexType type = node.getType();
      
      if(type.isType()) {
         return findMemberFunction(node, pattern);
      }
      return findFunctionInScope(node, pattern);
   }
   
   private IndexNode findFunctionInScope(IndexNode node, Pattern pattern) {
      try {
         Map<String, IndexNode> expandedScope = database.getNodesInScope(node);
         Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
         
         for(Entry<String, IndexNode> entry : entries) {
            String key = entry.getKey();
            Matcher matcher = pattern.matcher(key);
            
            if(matcher.matches()) {
               IndexNode match = entry.getValue();
               
               if(match != null) {
                  return findConstraint(match);
               }
            }
         }
      } catch(Throwable e) {
         log.info("Could not find function", e);
      }
      return null;
   }
   
   private IndexNode findMemberFunction(IndexNode node, Pattern pattern) {
      try {
         Map<String, IndexNode> expandedScope = database.getMemberNodes(node);
         Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
         
         for(Entry<String, IndexNode> entry : entries) {
            String key = entry.getKey();
            Matcher matcher = pattern.matcher(key);
            
            if(matcher.matches()) {
               IndexNode match = entry.getValue();
               
               if(match != null) {
                  return findConstraint(match);
               }
            }
         }
      } catch(Throwable e) {
         log.info("Could not find function", e);
      }
      return null;
   }
   
   private IndexNode findEnclosingThis(IndexNode node) {
      while(node != null) {
         IndexType type = node.getType();
         
         if(type.isType() && !type.isImport()) {
            return node;
         }
         node = node.getParent();
      }
      return node;
   }
   
   private IndexNode findEnclosingSuper(IndexNode node) {
      IndexNode base = findEnclosingThis(node);
      Set<IndexNode> nodes = base.getNodes();
      
      for(IndexNode entry : nodes) {
         IndexType type = entry.getType();
         
         if(type.isSuper()) {
            return entry;
         }
      }
      return null;
   }
   
   private IndexNode findConstraint(IndexNode node) throws Exception {
      if(node != null) {
         IndexNode constraint = node.getConstraint();
         
         if(constraint != null) {
            node = constraint;
         } else {
            IndexType type = node.getType();
            
            if(type.isImport()) {
               String name = node.getFullName();
               node = database.getTypeNode(name);
               
               if(node == null) {
                  node = database.getDefaultImport(null, name);
               }
            }
         }
      }
      return node;
   }
   
   private static Pattern createFunctionPattern(ExpressionToken token) {
      String name = token.getName();
      List<ExpressionArgument> arguments = token.getArguments();
      int count = arguments.size();
      
      if(count > 0) {
         StringBuilder builder = new StringBuilder(name);
      
         builder.append("\\(");
         
         for(int i = 0; i < count; i++) {
            if(i > 0) {
               builder.append("\\s*,\\s*");
            }
            builder.append("[a-zA-Z0-9_]+");
         }
         builder.append("\\)");
         String pattern = builder.toString();
         return Pattern.compile(pattern);
      }
      return Pattern.compile(name + "\\(\\)");
   }
   
}
