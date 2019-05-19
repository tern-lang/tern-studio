package org.ternlang.studio.core.command;

public class FolderCollapseCommandMarshaller extends ObjectCommandMarshaller<FolderCollapseCommand>{
   
   public FolderCollapseCommandMarshaller() {
      super(CommandType.FOLDER_COLLAPSE);
   }
}