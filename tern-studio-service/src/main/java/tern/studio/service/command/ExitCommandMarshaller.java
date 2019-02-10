package tern.studio.service.command;

public class ExitCommandMarshaller extends ObjectCommandMarshaller<ExitCommand>{

   public ExitCommandMarshaller() {
      super(CommandType.EXIT);
   }
}