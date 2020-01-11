package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.codegen.CodeAppender;

public abstract class PropertyGenerator {

   protected final Property property;
   protected final Entity entity;
   protected final Domain domain;

   protected PropertyGenerator(Domain domain, Entity entity, Property property) {
      this.domain = domain;
      this.entity = entity;
      this.property = property;
   }

   public Domain getDomain() {
      return domain;
   }

   public Property getProperty() {
      return property;
   }

   public Entity getEntity() {
      return entity;
   }

   public Entity getConstraintEntity() {
      Package module = entity.getPackage();

      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      Scope scope = module.getScope();
      ScopeState state = scope.getState();
      String constraint = property.getConstraint();
      Value value = state.getValue(constraint);

      return value.getValue();
   }

   public String getConstraint(Case textCase) {
      String constraint = property.getConstraint();
      char first = constraint.charAt(0);
      String remainder = constraint.substring(1);

      if(textCase == Case.CAMEL) {
         return String.format("%s%s", Character.toLowerCase(first), remainder);
      }
      if(textCase == Case.PASCAL) {
         return String.format("%s%s", Character.toUpperCase(first), remainder);
      }
      return constraint;
   }
   
   public abstract void generateField(CodeAppender builder);
   public abstract void generateGetter(CodeAppender builder);
   public abstract void generateSetter(CodeAppender builder);
   public abstract void generateGetterSignature(CodeAppender builder);
   public abstract void generateSetterSignature(CodeAppender builder);

   public enum Case {
      CAMEL,
      PASCAL,
      NORMAL
   }
}
