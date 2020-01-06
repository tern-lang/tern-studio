package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class Package {

   private final Cache<String, Entity> entities;
   private final String name;

   public Package(String name) {
      this.entities = new CopyOnWriteCache<String, Entity>();
      this.name = name;
   }
   
   public Entity addEntity(String name) {
      Entity entity = entities.fetch(name);
      
      if(entity == null) {
         entity = new Entity(name);
         entities.cache(name, entity);
      }
      return entity;
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
