package org.ternlang.studio.core.command;

public class AttachCommandMarshaller extends ObjectCommandMarshaller<AttachCommand>{
   
   public AttachCommandMarshaller() {
      super(CommandType.ATTACH);
   }
}