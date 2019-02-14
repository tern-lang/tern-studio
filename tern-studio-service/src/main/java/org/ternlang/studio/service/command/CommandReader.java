package org.ternlang.studio.service.command;

import java.util.HashMap;
import java.util.Map;

public class CommandReader {

   private final Map<String, CommandMarshaller> marshallers;

   public CommandReader() {
      this.marshallers = new HashMap<String, CommandMarshaller>();
   }
   
   public Command read(String text) throws Exception {
      if(marshallers.isEmpty()) {
         CommandType[] commands = CommandType.values();
         
         for(CommandType command : commands) {
            CommandMarshaller marshaller = command.marshaller.newInstance();
            String name = command.name();
            marshallers.put(name, marshaller);
         }
      }
      int offset = text.indexOf(':');
      String key = text;
      
      if(offset != -1) {
         key = text.substring(0, offset);
      }
      CommandMarshaller marshaller = marshallers.get(key);
      
      return marshaller.toCommand(text);
   }
}