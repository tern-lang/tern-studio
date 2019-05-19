package org.ternlang.studio.core.command;

public class DeleteCommandMarshaller extends ObjectCommandMarshaller<DeleteCommand>{
   
   public DeleteCommandMarshaller() {
      super(CommandType.DELETE);
   }
}