package org.ternlang.studio.core.command;

public class CreateArchiveCommandMarshaller extends ObjectCommandMarshaller<CreateArchiveCommand>{

   public CreateArchiveCommandMarshaller() {
      super(CommandType.CREATE_ARCHIVE);
   }
}