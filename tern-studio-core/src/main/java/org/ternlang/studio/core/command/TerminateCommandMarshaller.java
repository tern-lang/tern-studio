package org.ternlang.studio.core.command;

public class TerminateCommandMarshaller extends ObjectCommandMarshaller<TerminateCommand>{

   public TerminateCommandMarshaller() {
      super(CommandType.TERMINATE);
   }
}