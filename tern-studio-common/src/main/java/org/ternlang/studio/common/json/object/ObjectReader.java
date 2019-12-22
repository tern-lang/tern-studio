package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.entity.EntityMapper;
import org.ternlang.studio.common.json.entity.EntityReader;

public class ObjectReader {

   private final EntityMapper mapper;
   private final String root;
   
   public ObjectReader(EntityMapper mapper, Class type) {
      this.root = type.getSimpleName();
      this.mapper = mapper;
   }
   
   public <T> T read(String value) {
      EntityReader reader = mapper.read(root);
      return reader.read(value);
   }
}
