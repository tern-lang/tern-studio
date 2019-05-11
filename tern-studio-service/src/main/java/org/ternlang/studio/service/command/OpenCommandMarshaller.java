package org.ternlang.studio.service.command;

public class OpenCommandMarshaller extends ObjectCommandMarshaller<OpenCommand>{

   public OpenCommandMarshaller() {
      super(CommandType.OPEN);
   }
}
