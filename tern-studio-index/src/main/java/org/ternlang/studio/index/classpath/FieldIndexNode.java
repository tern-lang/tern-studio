package org.ternlang.studio.index.classpath;

import java.util.Collections;
import java.util.Set;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class FieldIndexNode implements IndexNode {

   private final ClassIndexNode parent;
   private final FieldInfo info;

   public FieldIndexNode(ClassIndexNode parent, FieldInfo info) {
      this.parent = parent;
      this.info = info;
   }

   @Override
   public int getLine() {
      return -1;
   }

   @Override
   public boolean isPublic() {
      return info.isPublic();
   }

   @Override
   public boolean isNative() {
      return true;
   }

   @Override
   public String getResource() {
      return parent.getResource();
   }

   @Override
   public String getAbsolutePath() {
      return parent.getAbsolutePath();
   }

   @Override
   public String getName() {
      return info.getName();
   }

   @Override
   public String getTypeName() {
      return info.getName();
   }

   @Override
   public String getFullName() {
      return info.getName();
   }

   @Override
   public String getModule() {
      return parent.getModule();
   }

   @Override
   public IndexNode getConstraint() {
      ClassIndexNodePath path = parent.getPath();
      String name = info.getTypeSignatureOrTypeDescriptor().toString();
      int index = name.indexOf("<");

      if(index != -1) {
         String type = name.substring(0, index);
         return path.getNode(type);
      }
      return path.getNode(name);
   }

   @Override
   public IndexNode getParent() {
      return parent;
   }

   @Override
   public IndexType getType() {
      return IndexType.PROPERTY;
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
