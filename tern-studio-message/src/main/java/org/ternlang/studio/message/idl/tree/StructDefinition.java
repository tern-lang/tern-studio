package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class StructDefinition implements Definition {

   private final StructProperty[] properties;
   private final TextLiteral name;
   
   public StructDefinition(TextLiteral name, StructProperty... properties) {
      this.properties = properties;
      this.name = name;
   }
}
