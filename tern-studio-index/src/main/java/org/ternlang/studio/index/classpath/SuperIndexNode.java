package org.ternlang.studio.index.classpath;

import java.util.Collections;
import java.util.Set;

import io.github.classgraph.ClassInfo;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class SuperIndexNode extends ClassIndexNode {

   public SuperIndexNode(ClassIndexNodePath path, ClassInfo info) {
      super(path, info);
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
