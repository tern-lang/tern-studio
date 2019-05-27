package org.ternlang.studio.core.command;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ObjectCommandMarshaller<T extends Command> implements CommandMarshaller<T> {

   private final ObjectMapper mapper;
   private final CommandType type;
   
   public ObjectCommandMarshaller(CommandType type) {
      this.mapper = new ObjectMapper();
      this.type = type;
   }

   @Override
   public T toCommand(String text) {
      int offset = text.indexOf(':');
      String json = text.substring(offset + 1);
      
      try {
         return (T)mapper.readValue(json, type.command);
      } catch(Exception e) {
         throw new IllegalStateException("Could not parse " + json + " for " + type.command, e);
      }
   }

   @Override
   public String fromCommand(T command) {
    String prefix = type + ":";
    
    try {  
      return prefix + mapper.writeValueAsString(command);
   } catch(Exception e) {
      throw new IllegalStateException("Could not generate JSON for " + type.command, e);
   }
   }
}