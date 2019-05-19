package org.ternlang.studio.resource.action.build;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.Predicate;

public class MapSystem implements DependencySystem {
   
   private final Function<Object, List<Object>> builder;
   private final Map<Class, List<Object>> groups;
   private final Set<Object> instances;
   private final ComponentMapper mapper;
   
   public MapSystem() {
      this.groups = new ConcurrentHashMap<Class, List<Object>>();
      this.builder = (key) -> new CopyOnWriteArrayList<Object>();
      this.instances = new CopyOnWriteArraySet<Object>();
      this.mapper = new ComponentMapper();
   }
   
   @Override
   public <T> List<T> resolveAll(Class<T> type) {
      return (List)groups.computeIfAbsent(type, builder);
   }
   
   @Override
   public <T> T resolve(Class<T> type) {
      return (T)instances.stream()
            .filter(Objects::nonNull)
            .filter(type::isInstance)
            .findFirst()
            .orElse(null);
   }

   @Override
   public <T> T resolve(Class<T> type, String name) {
      Predicate predicate = mapper.filter(name);
      return (T)instances.stream()
            .filter(Objects::nonNull)
            .filter(type::isInstance)
            .filter(predicate)
            .findFirst()
            .orElse(null);
   }

   @Override
   public void register(Object value) {
      instances.add(value);
      mapper.expand(value)
         .stream()
         .map(this::resolveAll)
         .forEach(list -> List.class.cast(list).add(value));
   }
}
