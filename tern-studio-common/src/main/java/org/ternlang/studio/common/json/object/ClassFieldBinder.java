package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.entity.Property;
import org.ternlang.studio.common.json.entity.PropertyConverter;

class ClassFieldBinder {
   
   private final PropertyConverter converter;
   
   public ClassFieldBinder(PropertyConverter converter) {
      this.converter = converter;
   }

   public Property bind(Field field, Class type, String name) {
      Class entry = type.getComponentType();
      
      if(!type.isArray()) {
         if(type == double.class) {
            return new DoubleProperty(field, type, name);  
         } 
         if(type == float.class) {
            return new FloatProperty(field, type, name);  
         } 
         if(type == long.class) {
            return new LongProperty(field, type, name);  
         } 
         if(type == int.class) {
            return new IntegerProperty(field, type, name);  
         } 
         if(type == short.class) {
            return new ShortProperty(field, type, name);  
         } 
         if(type == byte.class) {
            return new ByteProperty(field, type, name);  
         } 
         if(type == char.class) {
            return new CharacterProperty(field, type, name);  
         } 
         if(type == boolean.class) {
            return new BooleanProperty(field, type, name);  
         }
         if(type == String.class) {
            return new StringProperty(field, type, name);  
         }
         return new ObjectProperty(converter, field, type, name, false, false);
      }
      return new ObjectProperty(converter, field, entry, name, false, true);  
   }
}
 