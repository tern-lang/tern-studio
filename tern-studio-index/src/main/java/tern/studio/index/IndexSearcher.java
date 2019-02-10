package tern.studio.index;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;

import tern.studio.index.counter.BraceCounter;

@Slf4j
public class IndexSearcher implements SourceFile {

   private final IndexNodeComparator comparator;
   private final BraceCounter counter;
   private final IndexDatabase database;
   private final IndexNode node;
   private final String resource;
   private final String script;
   private final File file;
   
   public IndexSearcher(IndexDatabase database, BraceCounter counter, IndexNode node, File file, String resource, String script) {
      this.comparator = new IndexNodeComparator(true);
      this.resource = resource;
      this.database = database;
      this.counter = counter;
      this.script = script;
      this.node = node;
      this.file = file;
   }
   
   @Override
   public File getFile() {
      return file;
   }
   
   @Override
   public String getScriptPath() {
      return script;
   }
   
   @Override
   public String getRealPath() {
      return resource;
   }
   
   public int getDepthAtLine(int line) {
      return counter.getDepth(line);
   }
   
   @Override
   public IndexNode getRootNode(){
      return node;
   }
   
   @Override
   public IndexNode getNodeAtLine(int line) {
      int depth = counter.getDepth(line);
      
      if(depth > 0) {
         return getNode(node, line, depth);
      }
      return node;
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodes() {
      return getTypeNodes(node, null);
   }
   
   private Map<String, IndexNode> getTypeNodes(IndexNode parent, String prefix) {
      Set<IndexNode> nodes = parent.getNodes();
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> types = new HashMap<String, IndexNode>();
      
         for(IndexNode node : nodes) {
            IndexType type = node.getType();
            
            if(type.isType()) {
               String name = node.getName();
               String token = prefix == null ? name : prefix + "." + name; 
               Map<String, IndexNode> children = getTypeNodes(node, token);
               
               types.putAll(children);
               types.put(token, node);
            }
         }
         return types;
      }
      return Collections.emptyMap();
   }
   
   @Override
   public Map<String, IndexNode> getNodesInScope(int line) {
      IndexNode node = getNodeAtLine(line);
      
      if(node != null) {
         try {
            return database.getNodesInScope(node);
         } catch(Throwable e) {
            log.info("Could not get nodes in scope at line " + line, e);
         }
      }
      return Collections.emptyMap();
   }
   
   private IndexNode getNode(IndexNode node, int line, int depth) {
      int start = node.getLine();
      
      if(depth == 0 && start <= line) {
         return getBestNode(node, line);
      } 
      if(depth > 0){
         Set<IndexNode> nodes = node.getNodes();
         
         if(!nodes.isEmpty()) {
            Set<IndexNode> results = new TreeSet<IndexNode>(comparator);
            
            for(IndexNode entry : nodes) {
               IndexType type = entry.getType();
               
               if(!type.isLeaf()) {
                  IndexNode match = getNode(entry, line, depth -1);
                  
                  if(match != null) {
                     results.add(match);
                  }
               }
            }
            if(!results.isEmpty()) {
               return results.iterator().next();
            }
         }
      }
      return null;
   }
      
   private IndexNode getBestNode(IndexNode node, int line) {
      Set<IndexNode> nodes = node.getNodes();
      IndexNode best = node;
      
      for(IndexNode entry : nodes) {
         IndexType type = entry.getType();
         int start = entry.getLine();
         
         if(start <= line) {
            if(!type.isLeaf()) {
               int threshold = best.getLine();
   
               if(start >= threshold) {
                  best = entry;
               }
            }
         }
      }
      return best;
   }
   
   @Override
   public String toString() {
      return String.valueOf(node);
   }
}
