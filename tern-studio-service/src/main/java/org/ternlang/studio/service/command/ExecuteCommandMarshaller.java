package org.ternlang.studio.service.command;

public class ExecuteCommandMarshaller extends ObjectCommandMarshaller<ExecuteCommand>{
   
   public ExecuteCommandMarshaller() {
      super(CommandType.EXECUTE);
   }
}