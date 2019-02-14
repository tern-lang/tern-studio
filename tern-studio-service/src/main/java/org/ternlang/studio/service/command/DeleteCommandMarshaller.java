package org.ternlang.studio.service.command;

public class DeleteCommandMarshaller extends ObjectCommandMarshaller<DeleteCommand>{
   
   public DeleteCommandMarshaller() {
      super(CommandType.DELETE);
   }
}