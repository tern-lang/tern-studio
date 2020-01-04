package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class MapEntity implements Entity {

   private final List<Property> properties;
   private final Supplier<Object> factory;
   private final ClassFieldBinder builder;
   private final String entity;
   private final Class type;
   
   public MapEntity(PropertyConverter converter, Supplier<Object> factory, Class type, String entity) {
      this.builder = new ClassFieldBinder(converter);
      this.properties = new ArrayList<Property>();
      this.factory = factory;
      this.entity = entity;
      this.type = type;
   }
   
   public Property index(String name, Field field) {
//      Class type = field.getType();
//      Property property = attributes.match(name);
//      
//      if(property == null) {
//         Property binding = builder.bind(field, type, name);
//         
//         field.setAccessible(true);
//         attributes.index(binding, name);
//         properties.add(binding);
//         
//         return binding;
//      }
//      return property;
      return null;
   }
   
   @Override
   public Object getInstance() {
      return factory.get();
   }

   @Override
   public Iterable<Property> getProperties() {
      return Collections.emptyList();
   }

   @Override
   public Property getProperty(CharSequence name) {
      return null;
   }

   @Override
   public String getEntity() {
      return entity;
   }
   
   @Override
   public Class getType() {
      return type;
   }
}
