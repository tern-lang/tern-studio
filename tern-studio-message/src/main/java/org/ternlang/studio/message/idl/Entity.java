package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class Entity {

   private Cache<String, Property> properties;
   private EntityType type;
   private Package module;
   private String name;

   public Entity(String name) {
      this.properties = new CopyOnWriteCache<String, Property>();
      this.name = name;
   }
   
   public Property addProperty(String name) {
      Property property = properties.fetch(name);
      
      if(property == null) {
         property = new Property(name);
         properties.cache(name, property);
      }
      return property;
   }
   
   public Property getProperty(String name) {
      return properties.fetch(name);
   }

   public List<Property> getProperties() {
      return properties.keySet()
            .stream()
            .map(properties::fetch)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
   }

   public EntityType getType() {
      return type;
   }

   public Entity setType(EntityType type) {
      this.type = type;
      return this;
   }

   public String getName() {
      return name;
   }

   public Package getPackage() {
      return module;
   }

   public Entity setPackage(Package module) {
      this.module = module;
      return this;
   }
}
