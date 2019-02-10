package tern.studio.index.classpath.node;

import java.util.Collections;
import java.util.Set;

import tern.studio.index.IndexNode;
import tern.studio.index.IndexType;
import tern.studio.index.classpath.ClassFile;

public class SuperIndexNode extends ClassIndexNode {
   
   public SuperIndexNode(ClassFile file) {
      super(file);
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
