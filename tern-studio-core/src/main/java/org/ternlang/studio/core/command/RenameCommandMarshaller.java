package org.ternlang.studio.core.command;

public class RenameCommandMarshaller extends ObjectCommandMarshaller<RenameCommand>{
   
   public RenameCommandMarshaller() {
      super(CommandType.RENAME);
   }
}