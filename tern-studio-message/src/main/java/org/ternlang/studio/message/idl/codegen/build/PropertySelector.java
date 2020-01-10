package org.ternlang.studio.message.idl.codegen.build;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.Property;

public class PropertySelector {

   private final Domain domain;
   
   public PropertySelector(Domain domain) {
      this.domain = domain;
   }
   
   public PropertyGenerator select(Property property) {
      String constraint = property.getConstraint();
      
      if(property.isPrimitive()) {
         if(property.isArray()) {
            return new PrimitiveArrayGenerator(domain);
         }
         return new PrimitiveGenerator(domain);
      }
      Entity entity = domain.getEntity(constraint);
      
      if(entity == null) {
         throw new IllegalStateException("Could not find entity " + entity);
      }
      EntityType type = entity.getType();

      if(property.isArray()) {
         if(type.isEnum()) {
            return new EnumArrayGenerator(domain);
         }
         if(type.isUnion()) {
            return new UnionArrayGenerator(domain);
         }
         if(type.isStruct()) {
            return new StructArrayGenerator(domain);
         }
         throw new IllegalArgumentException("Could not create array property for " + entity);
      }      
      if(type.isEnum()) {
         return new EnumGenerator(domain);
      }
      if(type.isUnion()) {
         return new UnionGenerator(domain);
      }
      if(type.isStruct()) {
         return new StructGenerator(domain);
      }
      throw new IllegalArgumentException("Could not create property for " + entity);
   }
}
