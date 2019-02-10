package tern.studio.service.command;

public class PingCommandMarshaller implements CommandMarshaller<PingCommand>{

   @Override
   public PingCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String project = value.substring(offset + 1);
      
      return new PingCommand(project);
   }

   @Override
   public String fromCommand(PingCommand command) {
      String project = command.getProject();
      return CommandType.PING + ":" + project;
   }
}