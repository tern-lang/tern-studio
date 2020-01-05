package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.NumberLiteral;
import org.ternlang.tree.literal.TextLiteral;

public class ArrayConstraint implements Constraint {

   private final NumberLiteral[] dimensions;
   private final NameReference type;
   
   public ArrayConstraint(TextLiteral type, NumberLiteral... dimensions) {
      this.type = new NameReference(type);
      this.dimensions = dimensions;
   }

   @Override
   public void process(Scope scope, Property property) throws Exception {

   }
}
