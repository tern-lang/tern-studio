package org.ternlang.studio.common.json.entity;

import java.lang.reflect.Array;

import org.ternlang.studio.common.json.document.Value;

public class EntityWriter {

   private final PropertyConverter converter;
   private final EntityProvider provider;
   private final StringBuilder builder;

   public EntityWriter(EntityProvider provider, PropertyConverter converter) {
      this.builder = new StringBuilder();
      this.converter = converter;
      this.provider = provider;
   }

   public CharSequence write(Object source) {
      if(source != null) {
         Class type = source.getClass();
         String name = type.getSimpleName();
         Entity entity = provider.getEntity(name);

         builder.append("{");
         writeObject(source, entity);
         builder.append("}");
      } else {
         builder.append("null");
      }
      return builder;
   }
   
   private void writeObject(Object source, Entity entity) {
      if(source != null) {
         Iterable<Property> properties = entity.getProperties();
         String separator = "";
         
         for(Property property : properties) {
            String name = property.getName();
            Value value = property.getValue(source);
            
            builder.append(separator);
            builder.append('"');
            builder.append(name);
            builder.append("\":");
            
            if(property.isPrimitive()) {
               builder.append("\":\"");
               builder.append(value);
               builder.append("\"");
            } else if(property.isArray()) {
               Object array = property.getValue(source);
               
               if(array != null) {
                  int length = Array.getLength(array);
      
                  if(length > 0) {
                     writeArray(source, entity, length);
                  } else {
                     builder.append("[]");
                  }
               } else {
                  builder.append("null");
               }
            } else {
               String type = property.getEntity();
               Entity child = provider.getEntity(type);
               
               builder.append("\":{");
               writeObject(value, child);
               builder.append("}");
            }
            separator = ",";
         }
      } else {
         builder.append("null");
      }
   }
   
   
   private void writeArray(Object source, Entity entity, int length) {
      for(int i = 0; i < length; i++) {
         
      }
   }
   
}
