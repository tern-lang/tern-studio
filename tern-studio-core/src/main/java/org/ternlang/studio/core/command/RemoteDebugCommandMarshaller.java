package org.ternlang.studio.core.command;

public class RemoteDebugCommandMarshaller extends ObjectCommandMarshaller<RemoteDebugCommand>{

   public RemoteDebugCommandMarshaller() {
      super(CommandType.REMOTE_DEBUG);
   }
}