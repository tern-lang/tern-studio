package org.ternlang.studio.index.classpath.node;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;
import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassCategory;
import org.ternlang.studio.index.classpath.ClassIndexProcessor;

public class ClassIndexNode extends ClassFileNode {
   
   private Set<IndexNode> children;
   private String fullName;
   private String typeName;
   private String name;
   private Class type;
   
   public ClassIndexNode(ClassFile file) {
      super(file);
   }
   
   @Override
   public String getName() {
      if(name == null) {
         name = file.getShortName();
      }
      return name;
   }

   @Override
   public String getTypeName() {
      if(typeName == null) {
         typeName = file.getTypeName();
      }
      return typeName;
   }

   @Override
   public String getFullName() {
      if(fullName == null) {
         fullName = file.getFullName();
      }
      return fullName;
   }

   @Override
   public IndexNode getConstraint() {
      return null;
   }

   @Override
   public IndexNode getParent() {
      Class type = getNodeClass();
      
      if(type != null) {
         Class parent = type.getDeclaringClass();
         
         if(parent != null) {
            return ClassIndexProcessor.getIndexNode(parent);
         }
      }
      return null;
   }
   
   @Override
   public boolean isPublic() {
      int modifiers = file.getModifiers();
      return Modifier.isPublic(modifiers);
   }

   @Override
   public IndexType getType() {
      ClassCategory type = file.getCategory();;
      
      if(type != null) {
         if(type == ClassCategory.INTERFACE) {
            return IndexType.TRAIT;
         }
         if(type == ClassCategory.ENUM) {
            return IndexType.ENUM;
         }
      }
      return IndexType.CLASS;
   }

   @Override
   public Set<IndexNode> getNodes() {
      if(children == null) {
         children = ClassIndexProcessor.getChildren(file);
      }
      return children;
   }
   
   private Class getNodeClass() {
      if(type == null) {
         try {
            type = file.loadClass();
         } catch(Throwable e) {
            return null;
         }
      }
      return type;
   }
   
   @Override
   public String toString(){
      return getFullName();
   }
   
}
