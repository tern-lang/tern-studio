package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class UnionDefinition implements Definition {

   private final UnionProperty[] properties;
   private final NameReference identifier;
   
   public UnionDefinition(TextLiteral identifier, UnionProperty... properties) {
      this.identifier = new NameReference(identifier);
      this.properties = properties;
   }
   
   @Override
   public void process(Scope scope, Package module) throws Exception {
      String name = identifier.getName(scope);
      Entity entity = module.getEntity(name);
      String namespace = module.getName();
      
      entity.setModule(namespace);
      entity.setType(EntityType.UNION);
   }
}
