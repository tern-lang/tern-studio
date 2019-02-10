package tern.studio.index.classpath.node;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import tern.core.convert.PrimitivePromoter;
import tern.studio.index.IndexNode;
import tern.studio.index.IndexType;
import tern.studio.index.classpath.ClassFile;
import tern.studio.index.classpath.ClassIndexProcessor;

public class MethodIndexNode extends ClassFileNode {
   
   private static final String[] PREFIX = {
   "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
   "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
   
   private PrimitivePromoter promoter;
   private String description;
   private Method method;
   
   public MethodIndexNode(ClassFile file, Method method) {
      super(file);
      this.promoter = new PrimitivePromoter();
      this.method = method;
   }

   @Override
   public boolean isPublic(){
      int modifiers = method.getModifiers();
      return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
   }

   @Override
   public String getName() {
      if(description == null) {
         Class[] types = method.getParameterTypes();
         String name = method.getName();
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
      Class returnType = method.getReturnType();
      Class real = promoter.promote(returnType);
      return ClassIndexProcessor.getIndexNode(real);
   }

   @Override
   public IndexNode getParent() {
      Class parent = method.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.MEMBER_FUNCTION;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}