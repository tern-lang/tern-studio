package org.ternlang.studio.message.idl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;
import org.ternlang.core.scope.Scope;

public class Package {

   private Cache<String, Entity> entities;
   private Scope scope;
   private String name;
   private String path;

   public Package(String name) {
      this.entities = new CopyOnWriteCache<String, Entity>();
      this.name = name;
   }
   
   public Scope getScope() {
      return scope;
   }
   
   public void setScope(Scope scope) {
      this.scope = scope;
   }
   
   public String getName() {
      return name;
   }
   
   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
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
}
