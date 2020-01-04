package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class UnionDefinition implements Definition {

   private final UnionProperty[] properties;
   private final TextLiteral name;
   
   public UnionDefinition(TextLiteral name, UnionProperty... properties) {
      this.properties = properties;
      this.name = name;
   }
}
