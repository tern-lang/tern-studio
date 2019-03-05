package org.ternlang.studio.index.classpath;

import java.util.Collections;
import java.util.Set;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class SuperIndexNode implements IndexNode {

   private final IndexNode node;

   public SuperIndexNode(IndexNode node) {
      this.node = node;
   }

   @Override
   public int getLine() {
      return node.getLine();
   }

   @Override
   public boolean isPublic() {
      return node.isPublic();
   }

   @Override
   public boolean isNative() {
      return node.isNative();
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
      return node;
   }

   @Override
   public IndexNode getParent() {
      return node.getParent();
   }

   @Override
   public IndexType getType() {
      return IndexType.SUPER;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }

   @Override
   public String toString(){
      return getFullName();
   }
}
