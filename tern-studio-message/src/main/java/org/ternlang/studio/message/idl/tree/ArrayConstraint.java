package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.NumberLiteral;
import org.ternlang.tree.literal.TextLiteral;

public class ArrayConstraint implements Constraint {

   private final NumberLiteral dimension;
   private final NameReference type;
   private final Option option;
   
   public ArrayConstraint(TextLiteral type, NumberLiteral dimension) {
      this(type, dimension, null);
   }
   
   public ArrayConstraint(TextLiteral type, NumberLiteral dimension, Option option) {
      this.type = new NameReference(type);
      this.dimension = dimension;
      this.option = option;
   }

   @Override
   public void define(Scope scope, Property property) throws Exception {

   }
   
   @Override
   public void process(Scope scope, Property property) throws Exception {

   }
}
