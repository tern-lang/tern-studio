package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassEntity implements Entity {
   
   private final SymbolTable<FieldProperty> attributes;
   private final PropertyConverter converter;
   private final List<Property> properties;
   private final Supplier<Object> factory;
   private final String entity;
   private final Class type;
   
   public ClassEntity(PropertyConverter converter, Supplier<Object> factory, Class type, String entity) {
      this.attributes = new SymbolTable<FieldProperty>();
      this.properties = new ArrayList<Property>();
      this.converter = converter;
      this.factory = factory;
      this.entity = entity;
      this.type = type;
   }
   
   public Property index(String name, Field field) {
      Class type = field.getType();
      FieldProperty property = attributes.match(name);
      boolean primitive = converter.accept(type);
      
      if(property == null) {
         property = new FieldProperty(converter, field, name, primitive);  
         
         field.setAccessible(true);
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