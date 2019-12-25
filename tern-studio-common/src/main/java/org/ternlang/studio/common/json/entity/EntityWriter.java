package org.ternlang.studio.common.json.entity;

import java.lang.reflect.Array;

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
         
         if(converter.accept(type)) {
            builder.append("\"");
            builder.append(source);
            builder.append("\"");
         } else {
            String name = type.getSimpleName();
            Entity entity = provider.getEntity(name);
   
            builder.append("{");
            write(source, entity);
            builder.append("}");
         }
      } else {
         builder.append("null");
      }
      return builder;
   }
   
   private CharSequence write(Object source, Entity entity) {
      if(source != null) {
         Iterable<Property> properties = entity.getProperties();
         String separator = "";
         
         for(Property property : properties) {
            String name = property.getName();
            Object value = property.getValue(source);
            
            builder.append(separator);
            builder.append('"');
            builder.append(name);
            
            if(property.isPrimitive()) {
               builder.append("\":\"");
               builder.append(value);
               builder.append("\"");
            } else if(property.isArray()) {
               Object array = property.getValue(source);
               
               if(array != null) {
                  int length = Array.getLength(array);
                  
                  builder.append("\":[");
                  
                  for(int i = 0; i < length; i++) {
                     Object element = Array.get(array, i);
                     write(element);
                  }
                  builder.append("]");
               } else {
                  builder.append("null");
               }
            } else {
               String type = property.getEntity();
               Entity child = provider.getEntity(type);
               
               builder.append("\":{");
               write(value, child);
               builder.append("}");
            }
            separator = ",";
         }
      } else {
         builder.append("null");
      }
      return builder;
   }
}
