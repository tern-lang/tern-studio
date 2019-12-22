package org.ternlang.studio.common.json.object;

import org.ternlang.common.Cache;
import org.ternlang.common.CopyOnWriteCache;
import org.ternlang.studio.common.json.operation.BlockType;

public class ObjectMapper {
   
   private final Cache<Class, ObjectReader> builders;
   private final ValueConverter converter;
   private final ObjectBuilder builder;
   private final TypeIndexer indexer;
   private final BlockType match;
   private final char[] buffer;
   
   public ObjectMapper() {
      this.builders = new CopyOnWriteCache<Class, ObjectReader>();
      this.converter = new ValueConverter();
      this.builder = new ObjectBuilder();
      this.indexer = new TypeIndexer(converter, builder);
      this.match = new BlockType(null);
      this.buffer = new char[1024];
   }

   public ObjectMapper register(Class type) {
      indexer.index(type);
      return this;
   }
   
   public ObjectMapper match(String type) {
      if(type != null) {
         int length = type.length();
   
         type.getChars(0, length, buffer, 0);
         match.with(buffer, 0, length);
      } else {
         match.reset();
      }
      return this;
   }

   public ObjectReader resolve(Class type) {
      ObjectReader builder = builders.fetch(type);
      
      if(builder == null) {
         builder = create(type);
         builders.cache(type, builder);
      }
      return builder;
   }
   
   private ObjectReader create(Class type) {
      FieldElement tree = indexer.index(type);
      String root = type.getSimpleName();

      return new ObjectReader(indexer, converter, builder, match, root);
   }

}
