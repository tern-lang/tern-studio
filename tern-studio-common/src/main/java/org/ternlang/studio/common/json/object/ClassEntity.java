package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.ternlang.studio.common.json.entity.Entity;
import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassEntity implements Entity {
   
   private final SymbolTable<FieldProperty> attributes;
   private final PropertyConverter converter;
   private final Supplier<Object> factory;
   private final String name;
   private final Class type;
   
   public ClassEntity(PropertyConverter converter, Supplier<Object> factory, Class type, String name) {
      this.attributes = new SymbolTable<FieldProperty>();
      this.converter = converter;
      this.factory = factory;
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
   public Object getInstance() {
      return factory.get();
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