package tern.studio.index.complete;

import java.util.Set;

import tern.studio.index.IndexNode;
import tern.studio.index.IndexType;

public class ProxyIndexNode implements IndexNode {

   protected IndexNode node;
   
   public ProxyIndexNode(IndexNode node) {
      this.node = node;
   }

   @Override
   public int getLine() {
      return node.getLine();
   }
   
   @Override
   public boolean isNative() {
      return node.isNative();
   }
   
   @Override
   public boolean isPublic(){
      return node.isPublic();
   }

   @Override
   public String getResource() {
      return node.getResource();
   }

   @Override
   public String getAbsolutePath() {
      return node.getAbsolutePath();
   }

   @Override
   public String getName() {
      return node.getName();
   }

   @Override
   public String getTypeName() {
      return node.getTypeName();
   }

   @Override
   public String getFullName() {
      return node.getFullName();
   }

   @Override
   public String getModule() {
      return node.getModule();
   }

   @Override
   public IndexNode getConstraint() {
      return node.getConstraint();
   }

   @Override
   public IndexNode getParent() {
      return node.getParent();
   }

   @Override
   public IndexType getType() {
      return node.getType();
   }

   @Override
   public Set<IndexNode> getNodes() {
      return node.getNodes();
   }
}
