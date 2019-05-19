package org.ternlang.studio.core.command;

import java.util.HashMap;
import java.util.Map;

public class CommandWriter {

   private final Map<Class, CommandMarshaller> marshallers;
   
   public CommandWriter() {
      this.marshallers = new HashMap<Class, CommandMarshaller>();
   }
   
   public String write(Command object) throws Exception {
      Class type = object.getClass();
      
      if(!marshallers.containsKey(type)) {
         CommandType[] commands = CommandType.values();
         
         for(CommandType command : commands) {
            CommandMarshaller marshaller = command.marshaller.newInstance();
            marshallers.put(command.command, marshaller);
         }
      }
      CommandMarshaller marshaller = marshallers.get(type);
      
      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + type);
      }
      return marshaller.fromCommand(object);
   }
}