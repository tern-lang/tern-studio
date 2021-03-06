package org.ternlang.studio.index.classpath;

import java.util.Collections;
import java.util.Set;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;

public class ConstructorIndexNode implements IndexNode {

   private static final String[] PREFIX = {
     "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
     "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

   private final ClassIndexNode parent;
   private final MethodInfo info;

   public ConstructorIndexNode(ClassIndexNode parent, MethodInfo info) {
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
      MethodParameterInfo[] parameters = info.getParameterInfo();
      String name = parent.getName();
      StringBuilder builder = new StringBuilder();

      builder.append(name);
      builder.append("(");

      for(int i = 0; i < parameters.length; i++) {
         String parameter = PREFIX[i];

         if(i > 0) {
            builder.append(", ");
         }
         builder.append(parameter);
      }
      builder.append(")");
      return builder.toString();
   }

   @Override
   public String getTypeName() {
      return getName();
   }

   @Override
   public String getFullName() {
      return getName();
   }

   @Override
   public String getModule() {
      return parent.getModule();
   }

   @Override
   public IndexNode getConstraint() {
      return parent;
   }

   @Override
   public IndexNode getParent() {
      return parent;
   }

   @Override
   public IndexType getType() {
      return IndexType.CONSTRUCTOR;
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