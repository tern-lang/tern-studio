package org.ternlang.studio.service.command;

public class CreateArchiveCommandMarshaller extends ObjectCommandMarshaller<CreateArchiveCommand>{

   public CreateArchiveCommandMarshaller() {
      super(CommandType.CREATE_ARCHIVE);
   }
}