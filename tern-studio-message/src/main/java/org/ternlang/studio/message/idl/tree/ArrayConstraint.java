package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.NumberLiteral;
import org.ternlang.tree.literal.TextLiteral;

public class ArrayConstraint implements Constraint {

   private final NumberLiteral[] dimensions;
   private final TextLiteral type;
   
   public ArrayConstraint(TextLiteral type, NumberLiteral... dimensions) {
      this.dimensions = dimensions;
      this.type = type;
   }
}
