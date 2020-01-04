package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class EntityConstraint implements Constraint {

   private final TextLiteral type;
   
   public EntityConstraint(TextLiteral type) {
      this.type = type;
   }
}
