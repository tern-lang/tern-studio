package tern.studio.service.command;

public class StatusCommandMarshaller extends ObjectCommandMarshaller<StatusCommand>{
   
   public StatusCommandMarshaller() {
      super(CommandType.STATUS);
   }
}