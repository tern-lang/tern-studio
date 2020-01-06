package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class Option {

   private final NameReference identifier;
   
   public Option(TextLiteral identifier) {
      this.identifier = new NameReference(identifier);
   }
   
   public boolean optional() {
      return true;
   }
}
