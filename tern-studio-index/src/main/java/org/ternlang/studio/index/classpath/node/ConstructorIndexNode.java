package org.ternlang.studio.index.classpath.node;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;
import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassIndexProcessor;

public class ConstructorIndexNode extends ClassFileNode {
   
   private static final String[] PREFIX = {
   "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
   "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
   
   private Constructor constructor;
   private String description;
   
   public ConstructorIndexNode(ClassFile file, Constructor constructor) {
      super(file);
      this.constructor = constructor;
   }

   @Override
   public boolean isPublic(){
      int modifiers = constructor.getModifiers();
      return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
   }

   @Override
   public String getName() {
      if(description == null) {
         Class[] types = constructor.getParameterTypes();
         String name = constructor.getDeclaringClass().getSimpleName();
         StringBuilder builder = new StringBuilder();
         
         builder.append(name);
         builder.append("(");
         
         for(int i = 0; i < types.length; i++) {
            String parameter = PREFIX[i];
            
            if(i > 0) {
               builder.append(", ");
            }
            builder.append(parameter);
         }
         builder.append(")");
         description = builder.toString();
      }
      return description;
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
   public IndexNode getConstraint() {
      Class returnType = constructor.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(returnType);
   }

   @Override
   public IndexNode getParent() {
      Class parent = constructor.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.CONSTRUCTOR;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}