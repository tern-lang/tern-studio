package org.ternlang.studio.core.command;

public class ExecuteCommandMarshaller extends ObjectCommandMarshaller<ExecuteCommand>{
   
   public ExecuteCommandMarshaller() {
      super(CommandType.EXECUTE);
   }
}