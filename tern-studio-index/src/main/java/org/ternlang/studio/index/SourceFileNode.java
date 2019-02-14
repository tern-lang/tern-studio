package org.ternlang.studio.index;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.core.Reserved;

public class SourceFileNode implements IndexNode {
   
   private static final String DEFAULT_CONSTRAINT = Object.class.getName();

   private final AtomicReference<IndexNode> parent;
   private final Comparator<IndexNode> comparator;
   private final IndexDatabase database;
   private final Set<IndexNode> nodes;
   private final String resource;
   private final Index index;
   
   public SourceFileNode(IndexDatabase database, Index index, String resource) {
      this.parent = new AtomicReference<IndexNode>();
      this.comparator = new IndexNodeComparator();
      this.nodes = new TreeSet<IndexNode>(comparator);
      this.database = database;
      this.resource = resource;
      this.index = index;
   }
   
   @Override
   public int getLine() {
      return index.getLine();
   }
   
   @Override
   public boolean isNative() {
      return false;
   }
   
   @Override
   public boolean isPublic() {
      return true;
   }
   
   @Override
   public String getResource() {
      return resource;
   }
   
   @Override
   public String getAbsolutePath() {
      return null;
   }
   
   @Override
   public String getModule() {
      return index.getModule();
   }
   
   @Override
   public String getName() {
      IndexType type = index.getType();
      String name = index.getName();
      
      if(type.isConstructor()) {
         IndexNode parentNode = parent.get();
         String parentName = parentNode.getName();
         
         return name.replace(Reserved.TYPE_CONSTRUCTOR + "(", parentName + "(");
      }
      return name;
   }
   
   @Override
   public String getTypeName() {
      IndexType type = index.getType();
      String name = index.getName();

      if(type.isType()) {
         IndexNode parentNode = parent.get();
         IndexType parentType = parentNode.getType();
         
         if(parentType.isType() && !type.isRoot() && !type.isLeaf()) {
            return parentNode.getTypeName() + "." + name;
         }
         return name;
      }
      return name;
   }
   
   @Override
   public String getFullName() {
      IndexType type = index.getType();
      String name = index.getName();
      
      if(type.isImport()) {
         return index.getModule();
      }
      if(type.isSuper()) {
         try {
            Map<String, IndexNode> nodes = database.getImportsInScope(this);
            IndexNode node = nodes.get(name);
            
            if(node != null) {
               return node.getFullName();
            }
         }catch(Throwable e){
            e.printStackTrace();
         }
      }
      if(type.isType()) {
         IndexNode parentNode = parent.get();
         IndexType parentType = parentNode.getType();
         
         if(parentType.isType() && !type.isRoot() && !type.isLeaf()) {
            return parentNode.getFullName() + "." + name;
         }
         String module = index.getModule();
         
         if(!module.endsWith(name)) {
            return module + "." + name;
         }
         return module;
      }
      return name;
   }
   
   @Override
   public IndexNode getConstraint() {
      IndexType type = index.getType();
      
      if(type.isConstrained()) {
         String constraint = index.getConstraint();
         String module = index.getModule();
         
         if(constraint != null) {
            try {
               Map<String, IndexNode> nodes = database.getNodesInScope(this);
               IndexNode node = nodes.get(constraint);
               
               if(node == null) {
                  return getConstraint(module, constraint);
               } else {
                  if(node.getType().isImport()) {
                     String fullName = node.getFullName();
                     return getConstraint(module, fullName);
                  }
                  return node;
               }
            }catch(Throwable e){
               e.printStackTrace();
            }
         }
         return getConstraint(null, null);
      }
      return null;
   }
   
   private IndexNode getConstraint(String module, String fullName) {
      IndexNode node = null;
      
      if(node == null && fullName != null) {
         try {
            node = database.getTypeNode(fullName);
         } catch(Exception e) {}
      }
      if(node == null && fullName != null) {
         try {
            node = database.getDefaultImport(module, fullName);
         } catch(Exception e) {}
      }
      if(node == null) {
         try {
            node = database.getTypeNode(DEFAULT_CONSTRAINT);
         } catch(Exception e) {}
      }
      return node;
   }
   
   @Override
   public IndexNode getParent() {
      return parent.get();
   }
   
   public void setParent(IndexNode node) {
      parent.set(node);
   }
   
   @Override
   public IndexType getType() {
      return index.getType();
   }
   
   public Set<IndexType> getParentTypes() {
      return index.getType().getParentTypes();
   }
   
   public Set<IndexNode> getNodes() {
      return nodes;
   }
   
   @Override
   public String toString() {
      return index.toString();
   }
}

