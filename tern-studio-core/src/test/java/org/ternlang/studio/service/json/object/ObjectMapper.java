package org.ternlang.studio.service.json.object;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class ObjectMapper {
   
   private final Cache<Class, ObjectReader> builders;
   private final ValueConverter converter;
   private final ObjectBuilder builder;
   private final TypeIndexer indexer;
   
   public ObjectMapper() {
      this.builders = new CopyOnWriteCache<Class, ObjectReader>();
      this.converter = new ValueConverter();
      this.builder = new ObjectBuilder();
      this.indexer = new TypeIndexer(converter, builder);
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
      FieldElement tree = indexer.index(type);
      return new ObjectReader(tree, converter, builder);
   }

}
