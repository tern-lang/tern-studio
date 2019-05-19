package org.ternlang.studio.core.command;

public class BrowseCommandMarshaller extends ObjectCommandMarshaller<BrowseCommand> {

   public BrowseCommandMarshaller() {
      super(CommandType.BROWSE);
   }
}