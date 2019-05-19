package org.ternlang.studio.core.command;

public class OpenCommandMarshaller extends ObjectCommandMarshaller<OpenCommand>{

   public OpenCommandMarshaller() {
      super(CommandType.OPEN);
   }
}
