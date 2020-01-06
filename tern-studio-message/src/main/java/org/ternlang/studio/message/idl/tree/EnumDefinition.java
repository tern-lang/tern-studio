package org.ternlang.studio.message.idl.tree;

import org.ternlang.core.scope.Scope;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.tree.NameReference;
import org.ternlang.tree.literal.TextLiteral;

public class EnumDefinition implements Definition {

   private final EnumProperty[] properties;
   private final NameReference identifier;
   
   public EnumDefinition(TextLiteral identifier, EnumProperty... properties) {
      this.identifier = new NameReference(identifier);
      this.properties = properties;
   }
   
   @Override
   public void define(Scope scope, Package module) throws Exception {
      String name = identifier.getName(scope);
      Entity entity = module.addEntity(name);
      String namespace = module.getName();
      
      entity.setModule(namespace);
      entity.setType(EntityType.ENUM);
      
      if(properties == null || properties.length == 0) {
         throw new IllegalStateException("Union " + name + " has no entities");
      }
      for(EnumProperty property : properties) {
         property.define(scope, entity);
      }
   }

   @Override
   public void process(Scope scope, Package module) throws Exception {
      String name = identifier.getName(scope);
      Entity entity = module.getEntity(name);
      
      if(entity == null) {
         throw new IllegalStateException("Enum " + name + " has not been defined");
      }
      for(EnumProperty property : properties) {
         property.process(scope, entity);
      }
   }
}
