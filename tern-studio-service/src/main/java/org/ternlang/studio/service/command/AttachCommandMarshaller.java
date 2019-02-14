package org.ternlang.studio.service.command;

public class AttachCommandMarshaller extends ObjectCommandMarshaller<AttachCommand>{
   
   public AttachCommandMarshaller() {
      super(CommandType.ATTACH);
   }
}