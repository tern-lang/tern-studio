package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class EnumProperty {

   private final TextLiteral identifier;
   
   public EnumProperty(TextLiteral identifier) {
      this.identifier = identifier;
   }
}
