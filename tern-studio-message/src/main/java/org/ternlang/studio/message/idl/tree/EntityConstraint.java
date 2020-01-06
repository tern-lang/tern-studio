package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class EntityConstraint implements Constraint {

   private final NameReference type;
   private final Option option;

   public EntityConstraint(TextLiteral type) {
      this(type, null);
   }
   
   public EntityConstraint(TextLiteral type, Option option) {
      this.type = new NameReference(type);
      this.option = option;
   }
   
   @Override
   public void define(Scope scope, Property property) throws Exception {

   }

   @Override
   public void process(Scope scope, Property property) throws Exception {
  
   }
}
