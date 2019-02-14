package org.ternlang.studio.index.classpath.node;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import org.ternlang.core.convert.PrimitivePromoter;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;
import org.ternlang.studio.index.classpath.ClassFile;
import org.ternlang.studio.index.classpath.ClassIndexProcessor;

public class FieldIndexNode extends ClassFileNode {

   private final PrimitivePromoter promoter;
   private final Field field;
   
   public FieldIndexNode(ClassFile file, Field field) {
      super(file);
      this.promoter = new PrimitivePromoter();
      this.field = field;
   }
   
   @Override
   public boolean isPublic(){
      int modifiers = field.getModifiers();
      return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
   }

   @Override
   public String getName() {
      return field.getName();
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
      Class type = field.getType();
      Class real = promoter.promote(type);
      return ClassIndexProcessor.getIndexNode(real);
   }

   @Override
   public IndexNode getParent() {
      Class parent = field.getDeclaringClass();
      return ClassIndexProcessor.getIndexNode(parent);
   }

   @Override
   public IndexType getType() {
      return IndexType.PROPERTY;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
}