package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class EntityConstraint implements Constraint {

   private final NameReference type;
   
   public EntityConstraint(TextLiteral type) {
      this.type = new NameReference(type);
   }

   @Override
   public void process(Scope scope, Property property) throws Exception {
  
   }
}
