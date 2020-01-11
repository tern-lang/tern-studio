package org.ternlang.studio.message.idl.codegen.build;

import java.util.List;
import java.util.stream.Collectors;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.Property;

public class PropertyGeneratorBuilder {

   private final Domain domain;
   
   public PropertyGeneratorBuilder(Domain domain) {
      this.domain = domain;
   }

   public List<PropertyGenerator> create(Entity entity) {
      return entity.getProperties()
          .stream()
          .map(this::create)
          .collect(Collectors.toList());
   }

   private PropertyGenerator create(Property property) {
      String constraint = property.getConstraint();
      
      if(property.isPrimitive()) {
         if(property.isArray()) {
            return new PrimitiveArrayGenerator(domain, property);
         }
         return new PrimitiveGenerator(domain, property);
      }
      Entity entity = domain.getEntity(constraint);
      
      if(entity == null) {
         throw new IllegalStateException("Could not find entity " + entity);
      }
      EntityType type = entity.getType();

      if(property.isArray()) {
         if(type.isEnum()) {
            return new EnumArrayGenerator(domain, property);
         }
         if(type.isUnion()) {
            return new UnionArrayGenerator(domain, property);
         }
         if(type.isStruct()) {
            return new StructArrayGenerator(domain, property);
         }
         throw new IllegalArgumentException("Could not create array property for " + entity);
      }      
      if(type.isEnum()) {
         return new EnumGenerator(domain, property);
      }
      if(type.isUnion()) {
         return new UnionGenerator(domain, property);
      }
      if(type.isStruct()) {
         return new StructGenerator(domain, property);
      }
      throw new IllegalArgumentException("Could not create property for " + entity);
   }
}
