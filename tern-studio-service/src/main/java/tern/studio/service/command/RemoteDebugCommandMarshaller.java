package tern.studio.service.command;

public class RemoteDebugCommandMarshaller extends ObjectCommandMarshaller<RemoteDebugCommand>{

   public RemoteDebugCommandMarshaller() {
      super(CommandType.REMOTE_DEBUG);
   }
}