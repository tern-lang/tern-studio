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
          .map(property -> create(entity, property))
          .collect(Collectors.toList());
   }

   private PropertyGenerator create(Entity parent, Property property) {
      String constraint = property.getConstraint();
      
      if(property.isPrimitive()) {
         if(property.isArray()) {
            return new PrimitiveArrayGenerator(domain, parent, property);
         }
         return new PrimitiveGenerator(domain, parent, property);
      }
      Entity entity = domain.getEntity(constraint);
      
      if(entity == null) {
         throw new IllegalStateException("Could not find entity " + entity);
      }
      EntityType type = entity.getType();

      if(property.isArray()) {
         if(type.isEnum()) {
            return new EnumArrayGenerator(domain, parent, property);
         }
         if(type.isUnion()) {
            return new UnionArrayGenerator(domain, parent, property);
         }
         if(type.isStruct()) {
            return new StructArrayGenerator(domain, parent, property);
         }
         throw new IllegalArgumentException("Could not create array property for " + entity);
      }      
      if(type.isEnum()) {
         return new EnumGenerator(domain, parent, property);
      }
      if(type.isUnion()) {
         return new UnionGenerator(domain, parent, property);
      }
      if(type.isStruct()) {
         return new StructGenerator(domain, parent, property);
      }
      throw new IllegalArgumentException("Could not create property for " + entity);
   }
}
