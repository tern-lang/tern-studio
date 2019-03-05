package org.ternlang.studio.index.classpath;

import java.util.Set;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class CacheIndexNode implements IndexNode {

   private Integer line;
   private Boolean isPublic;
   private Boolean isNative;
   private String resource;
   private String absolutePath;
   private String name;
   private String typeName;
   private String fullName;
   private String module;
   private IndexNode constraint;
   private IndexNode parent;
   private IndexType type;
   private Set<IndexNode> nodes;
   private IndexNode node;
   private String string;

   public CacheIndexNode(IndexNode node) {
      this.node = node;
   }

   @Override
   public int getLine() {
      if(line == null) {
         line = node.getLine();
      }
      return line;
   }

   @Override
   public boolean isPublic() {
      if(isPublic == null) {
         isPublic = node.isPublic();
      }
      return isPublic;
   }

   @Override
   public boolean isNative() {
      if(isNative == null) {
         isNative = node.isNative();
      }
      return isNative;
   }

   @Override
   public String getResource() {
      if(resource == null) {
         resource = node.getResource();
      }
      return resource;
   }

   @Override
   public String getAbsolutePath() {
      if(absolutePath == null) {
         absolutePath = node.getAbsolutePath();
      }
      return absolutePath;
   }

   @Override
   public String getName() {
      if(name == null) {
         name = node.getName();
      }
      return name;
   }

   @Override
   public String getTypeName() {
      if(typeName == null) {
         typeName = node.getTypeName();
      }
      return typeName;
   }

   @Override
   public String getFullName() {
      if(fullName == null) {
         fullName = node.getFullName();
      }
      return fullName;
   }

   @Override
   public String getModule() {
      if(module == null) {
         module = node.getModule();
      }
      return module;
   }

   @Override
   public IndexNode getConstraint() {
      if(constraint == null) {
         constraint = node.getConstraint();
      }
      return constraint;
   }

   @Override
   public IndexNode getParent() {
      if(parent == null) {
         parent = node.getParent();
      }
      return parent;
   }

   @Override
   public IndexType getType() {
      if(type == null) {
         type = node.getType();
      }
      return type;
   }

   @Override
   public Set<IndexNode> getNodes() {
      if(nodes == null) {
         nodes = node.getNodes();
      }
      return nodes;
   }

   @Override
   public String toString() {
      if(string == null) {
         string = node.toString();
      }
      return string;
   }
}
