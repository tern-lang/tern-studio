package org.ternlang.studio.service.json.object;

import java.util.concurrent.atomic.AtomicReference;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;

public class ObjectMapper {
   
   private final Cache<Class, ObjectReader> builders;
   private final AtomicReference<String> reference;
   private final ValueConverter converter;
   private final ObjectBuilder builder;
   private final TypeIndexer indexer;
   
   public ObjectMapper() {
      this.reference = new AtomicReference<String>();
      this.builders = new CopyOnWriteCache<Class, ObjectReader>();
      this.converter = new ValueConverter();
      this.builder = new ObjectBuilder();
      this.indexer = new TypeIndexer(converter, builder);
   }

   public ObjectMapper type(String type) throws Exception {
      reference.set(type);
      return this;
   }

   public ObjectMapper register(Class type) throws Exception {
      indexer.index(type);
      return this;
   }
   
   public ObjectReader resolve(Class type) throws Exception {   
      ObjectReader builder = builders.fetch(type);
      
      if(builder == null) {
         builder = create(type);
         builders.cache(type, builder);
      }
      return builder;
   }
   
   private ObjectReader create(Class root) throws Exception {
      FieldElement tree = indexer.index(root);
      String type = reference.get();
      String name = root.getSimpleName();

      return new ObjectReader(indexer, converter, builder, name, type);
   }

}
