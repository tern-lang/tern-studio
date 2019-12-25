package org.ternlang.studio.common.json.object;

import java.util.HashMap;
import java.util.Map;

import org.ternlang.studio.common.json.entity.EntityMapper;
import org.ternlang.studio.common.json.entity.PropertyConverter;

public class ObjectMapper {
   
   private final Map<Class, ObjectReader> readers;
   private final PropertyConverter converter;
   private final ObjectBuilder builder;
   private final ClassProvider provider;
   private final EntityMapper mapper;
   
   public ObjectMapper() {
      this.readers = new HashMap<Class, ObjectReader>();
      this.converter = new PropertyConverter();
      this.builder = new ObjectBuilder();
      this.provider = new ClassProvider(builder, converter);
      this.mapper = new EntityMapper(provider);
   }

   public ObjectMapper register(Class type) {
      provider.index(type);
      return this;
   }
   
   public ObjectMapper register(Class type, String alias) {
      provider.index(type, alias);
      return this;
   }
   
   public ObjectMapper match(String attribute) {
      mapper.match(attribute);
      return this;
   }

   public ObjectReader read(Class type) {
      provider.index(type);
      return readers.computeIfAbsent(type, 
            key -> new ObjectReader(mapper, type));
   }
}
