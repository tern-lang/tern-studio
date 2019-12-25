package org.ternlang.studio.common.json.entity;

public class EntityWriter {

   private final EntityProvider provider;
   private final StringBuilder builder;

   public EntityWriter(EntityProvider provider) {
      this.builder = new StringBuilder();
      this.provider = provider;
   }

   public CharSequence write(Object source) {
      Class type = source.getClass();
      String name = type.getSimpleName();
      Entity entity = provider.getEntity(name);
      
      builder.append("{");
      write(source, entity);
      builder.append("}");
      return builder;
   }
   
   private CharSequence write(Object source, Entity entity) {
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
            builder.append("\":[");
            builder.append(value);
            builder.append("]"); 
         } else {
            String type = property.getEntity();
            Entity child = provider.getEntity(type);
            
            builder.append("\":{");
            write(value, child);
            builder.append("}");
         }
         separator = ",";
      }
      return builder;
   }
}
