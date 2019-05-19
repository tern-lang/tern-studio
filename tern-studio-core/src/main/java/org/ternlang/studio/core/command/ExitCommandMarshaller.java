package org.ternlang.studio.core.command;

public class ExitCommandMarshaller extends ObjectCommandMarshaller<ExitCommand>{

   public ExitCommandMarshaller() {
      super(CommandType.EXIT);
   }
}