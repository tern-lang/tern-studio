package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassEntity implements Entity {
   
   private final SymbolTable<FieldProperty> attributes;
   private final PropertyConverter converter;
   private final ObjectBuilder builder;
   private final String name;
   private final Class type;
   
   public ClassEntity(ObjectBuilder builder, PropertyConverter converter, Class type, String name) {
      this.attributes = new SymbolTable<FieldProperty>();
      this.converter = converter;
      this.builder = builder;
      this.type = type;
      this.name = name;
   }
   
   public Property index(CharSequence name, Field field) {
      FieldProperty property = attributes.match(name);
      
      if(property == null) {
         property = new FieldProperty(converter, field);  
         
         field.setAccessible(true);
         attributes.index(property, name);
      }
      return property;
   }

   @Override
   public Object getInstance(CharSequence type) {
      return builder.create(type);
   }
   
   @Override
   public Property getProperty(CharSequence name) {
      return attributes.match(name);
   }

   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public Class getType() {
      return type;
   }
}