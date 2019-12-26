package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassEntity implements Entity {
   
   private final SymbolTable<Property> attributes;
   private final PropertyConverter converter;
   private final List<Property> properties;
   private final Supplier<Object> factory;
   private final String entity;
   private final Class type;
   
   public ClassEntity(PropertyConverter converter, Supplier<Object> factory, Class type, String entity) {
      this.attributes = new SymbolTable<Property>();
      this.properties = new ArrayList<Property>();
      this.converter = converter;
      this.factory = factory;
      this.entity = entity;
      this.type = type;
   }
   
   public Property index(String name, Field field) {
      Class type = field.getType();
      Property property = attributes.match(name);
      
      if(property == null) {
         Class entry = type.getComponentType();
         boolean primitive = converter.accept(type);
         
         field.setAccessible(true);
         
         if(type.isArray()) {
            property = new ObjectProperty(converter, field, entry, name, primitive, true);  
         } else {
            property = new ObjectProperty(converter, field, type, name, primitive, false);  
         }
         attributes.index(property, name);
         properties.add(property);
      }
      return property;
   }

   @Override
   public Object getInstance() {
      return factory.get();
   }
   
   @Override
   public Iterable<Property> getProperties() {
      return properties;
   }
   
   @Override
   public Property getProperty(CharSequence name) {
      return attributes.match(name);
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