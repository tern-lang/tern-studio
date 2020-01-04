package org.ternlang.studio.message.idl.tree;

import org.ternlang.tree.literal.TextLiteral;
 
public class StructProperty {

   private final TextLiteral identifier;
   private final Constraint constraint;
   
   public StructProperty(TextLiteral identifier, Constraint constraint) {
      this.constraint = constraint;
      this.identifier = identifier;
   }
}
