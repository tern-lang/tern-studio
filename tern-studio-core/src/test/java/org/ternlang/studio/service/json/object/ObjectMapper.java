package org.ternlang.studio.service.json.object;

import java.lang.reflect.Constructor;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class ObjectMapper {
   
   private final Cache<Class, ObjectReader> builders;
   
   public ObjectMapper() {
      this.builders = new CopyOnWriteCache<Class, ObjectReader>();
   }
   
   public ObjectReader resolve(Class type) throws Exception {   
      ObjectReader builder = builders.fetch(type);
      
      if(builder == null) {
         builder = create(type);
         builders.cache(type, builder);
      }
      return builder;
   }
   
   private ObjectReader create(Class type) throws Exception {   
      Constructor constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      TypeIndexer indexer = new TypeIndexer(constructor);
      FieldTree tree = indexer.index();
      return new ObjectReader(tree);
   }

}
