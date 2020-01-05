package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Property;
import org.ternlang.studio.message.idl.PropertyType;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class EnumProperty {

   private final NameReference identifier;
   
   public EnumProperty(TextLiteral identifier) {
      this.identifier = new NameReference(identifier);
   }
   
   public void process(Scope scope, Entity entity) throws Exception {
      String name = identifier.getName(scope);
      Property property = entity.getProperty(name);
      
      property.setType(PropertyType.ENUM);
   }
}
