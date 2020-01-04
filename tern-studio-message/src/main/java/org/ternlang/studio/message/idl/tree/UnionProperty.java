package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class UnionProperty {

   private final TextLiteral identifier;
   
   public UnionProperty(TextLiteral identifier) {
      this.identifier = identifier;
   }
}
