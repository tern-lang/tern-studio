package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.LazyCache;

public class Entity {

   private Cache<String, Property> properties;
   private EntityType type;
   private String name;
   private String module;

   public Entity(String name) {
      this.properties = new LazyCache<String, Property>(Property::new);
      this.name = name;
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

   public String getModule() {
      return module;
   }

   public Entity setModule(String module) {
      this.module = module;
      return this;
   }
}
