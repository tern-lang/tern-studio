package org.ternlang.studio.service.command;

public class FolderCollapseCommandMarshaller extends ObjectCommandMarshaller<FolderCollapseCommand>{
   
   public FolderCollapseCommandMarshaller() {
      super(CommandType.FOLDER_COLLAPSE);
   }
}