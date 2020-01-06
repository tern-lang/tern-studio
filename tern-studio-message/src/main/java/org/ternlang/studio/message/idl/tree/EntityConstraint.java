package org.ternlang.studio.message.idl.tree;

import static org.ternlang.studio.message.idl.PropertyType.ENTITY;
import static org.ternlang.studio.message.idl.PropertyType.PRIMITIVE;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.PropertyType;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class EntityConstraint implements Constraint {
   
   private final NameReference type;
   private final Option option;

   public EntityConstraint(TextLiteral type) {
      this(type, null);
   }

   public EntityConstraint(TextLiteral type, Option option) {
      this.type = new NameReference(type);
      this.option = option;
   }

   @Override
   public void define(Scope scope, Property property) throws Exception {
      String constraint = type.getName(scope);

      if(isPrimitive(constraint)) {
         property.setType(PRIMITIVE.mask);
      } else {
         property.setType(ENTITY.mask);
      }
      property.setConstraint(constraint);
      property.setOptional(option == null ? false : option.optional());
   }

   @Override
   public void process(Scope scope, Property property) throws Exception {
      int type = property.getType();
      
      if(PropertyType.isEntity(type)) {
         String constraint = property.getConstraint();
         ScopeState state = scope.getState();
         Value value = state.getValue(constraint);
         
         if(value == null) {
            throw new IllegalStateException("Unknown constraint '" + constraint + "'");
         }
      }
   }
}
