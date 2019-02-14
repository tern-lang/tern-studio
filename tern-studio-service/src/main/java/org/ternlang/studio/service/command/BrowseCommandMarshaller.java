package org.ternlang.studio.service.command;

public class BrowseCommandMarshaller extends ObjectCommandMarshaller<BrowseCommand> {

   public BrowseCommandMarshaller() {
      super(CommandType.BROWSE);
   }
}