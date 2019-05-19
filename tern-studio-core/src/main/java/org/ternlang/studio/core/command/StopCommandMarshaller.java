package org.ternlang.studio.core.command;

public class StopCommandMarshaller implements CommandMarshaller<StopCommand> {

   @Override
   public StopCommand toCommand(String text) {
      return new StopCommand();
   }

   @Override
   public String fromCommand(StopCommand command) {
      return CommandType.STOP.name();
   }
}