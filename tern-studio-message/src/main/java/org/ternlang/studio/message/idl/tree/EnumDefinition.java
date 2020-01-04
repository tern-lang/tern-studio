package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;

public class EnumDefinition implements Definition {

   private final EnumProperty[] properties;
   private final TextLiteral name;
   
   public EnumDefinition(TextLiteral name, EnumProperty... properties) {
      this.properties = properties;
      this.name = name;
   }
}
