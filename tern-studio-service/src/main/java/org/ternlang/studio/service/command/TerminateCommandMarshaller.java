package org.ternlang.studio.service.command;

public class TerminateCommandMarshaller extends ObjectCommandMarshaller<TerminateCommand>{

   public TerminateCommandMarshaller() {
      super(CommandType.TERMINATE);
   }
}