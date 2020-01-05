package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.LazyCache;

public class Package {

   private Cache<String, Entity> entities;
   private String name;

   public Package(String name) {
      this.entities = new LazyCache<String, Entity>(Entity::new);
      this.name = name;
   }
   
   public Entity getEntity(String name) {
      return entities.fetch(name);
   }

   public List<Entity> getEntities() {
      return entities.keySet()
            .stream()
            .map(entities::fetch)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
   } 
   
   public String getName() {
      return name;
   }
}
