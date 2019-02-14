package org.ternlang.studio.index.complete;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.ternlang.core.Reserved;
import org.ternlang.core.link.ImportPathResolver;
import org.ternlang.studio.index.IndexDatabase;
import org.ternlang.studio.index.IndexDumper;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexNodeSorter;
import org.ternlang.studio.index.IndexType;
import org.ternlang.studio.index.SourceFile;

public class CompletionCompiler {

   private final Class<? extends CompletionFinder>[] finders;
   private final ImportPathResolver importResolver;
   private final IndexDatabase database;
   
   public CompletionCompiler(IndexDatabase database, Class<? extends CompletionFinder>... finders) {
      this.importResolver = new ImportPathResolver(Reserved.IMPORT_FILE); 
      this.database = database;
      this.finders = finders;
   }
   
   public CompletionOutlineResponse completeOutline(CompletionRequest request) throws Exception {
      int line = request.getLine();
      List<EditContext> inputs = EditContextExtractor.extractContext(request);

      for(EditContext input : inputs) {
         String source = input.getSource();
         String resource = request.getResource();
         String complete = request.getComplete();
         SourceFile file = database.getFile(resource, source);
         IndexNode node = file.getNodeAtLine(line);
         IndexNode root = file.getRootNode();
         String details = IndexDumper.dump(root, node, complete);

         while (node != null) {
            IndexType type = node.getType();

            if (type.isType() && !type.isImport()) {
               Map<String, IndexNode> nodes = database.getMemberNodes(node);
               Map<String, CompletionOutline> outlines = completeOutlineTokens(complete, nodes);

               return new CompletionOutlineResponse(outlines, complete, details);
            }
            node = node.getParent();
         }
         Map<String, IndexNode> nodes = database.getNodesInScope(root);
         Map<String, CompletionOutline> outlines = completeOutlineTokens(complete, nodes);

         if(!outlines.isEmpty()) {
            return new CompletionOutlineResponse(outlines, complete, details);
         }
      }
      return new CompletionOutlineResponse();
   }
   
   public CompletionResponse completeExpression(CompletionRequest request) throws Exception {
      int line = request.getLine();
      List<EditContext> inputs = EditContextExtractor.extractContext(request);

      for(EditContext input : inputs) {
         String source = input.getSource();
         String resource = request.getResource();
         String complete = request.getComplete();
         SourceFile file = database.getFile(resource, source);
         IndexNode node = file.getNodeAtLine(line);
         IndexNode root = file.getRootNode();
         String details = IndexDumper.dump(root, node, complete);

         for (Class<? extends CompletionFinder> finderType : finders) {
            CompletionFinder finder = finderType.newInstance();
            InputExpression text = finder.parseExpression(input);

            if (text != null) {
               Set<IndexNode> matches = finder.findMatches(database, node, text);
               List<IndexNode> sortedMatches = IndexNodeSorter.sort(matches, resource);
               Map<String, IndexNode> nodes = database.getTypeNodes();
               Map<String, String> tokens = completeExpressionTokens(sortedMatches, nodes);
               String expression = input.getExpression();

               if(!tokens.isEmpty()) {
                  return new CompletionResponse(tokens, expression, details);
               }
            }
         }
      }
      return new CompletionResponse();
   }
   
   private Map<String, CompletionOutline> completeOutlineTokens(String complete, Map<String, IndexNode> nodes) {
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      String filter = complete.toLowerCase();
      
      if(!entries.isEmpty()) {
         Map<String, CompletionOutline> tokens = new TreeMap<String, CompletionOutline>(String.CASE_INSENSITIVE_ORDER);
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            IndexNode match = entry.getValue();
            
            if(name.toLowerCase().startsWith(filter)) {
               IndexType type = match.getType();
               
               if(type == IndexType.MEMBER_FUNCTION) {
                  type = IndexType.FUNCTION;
               }
               if(type.isMember()) {
                  IndexNode constraintNode = match.getConstraint();
                  String constraint = Object.class.getName();
                  IndexNode parent = match.getParent();
                  String declaringClass = null;
                  String libraryPath = null;
                  
                  if(parent != null) {
                     declaringClass = parent.getFullName();
                  }
                  if(match.isNative()) {
                     libraryPath = match.getAbsolutePath();
                  }
                  if(constraintNode != null) {
                     constraint = constraintNode.getFullName();
                  }
                  //String constraintName = importResolver.resolveName(constraint);
                  String constraintName = constraint;
                  String resource = match.getResource();
                  int line = match.getLine();
                  
                  CompletionOutline outline = new CompletionOutline(type, constraintName, libraryPath, declaringClass, resource, line);
                  tokens.put(name, outline);
               }
            }
         }
         return tokens;
      }
      return Collections.emptyMap();
   }

   private Map<String, String> completeExpressionTokens(List<IndexNode> matches, Map<String, IndexNode> nodes) {
      Map<String, String> tokens = new LinkedHashMap<String, String>();
      
      for(IndexNode match : matches) {
         String name = match.getName();
         IndexType type = match.getType();
         String fullName = match.getFullName();
         
         if(type == IndexType.MEMBER_FUNCTION) {
            type = IndexType.FUNCTION;
         }
         if(type == IndexType.IMPORT) {
            IndexNode imported = nodes.get(fullName);
            
            if(imported != null) {
               name = imported.getName();
               type = imported.getType();
            } else {
               type = IndexType.CLASS; // hack job
            }
         }
         String category = type.getName();
         tokens.put(name, category);
      }
      return tokens;
   }
}
