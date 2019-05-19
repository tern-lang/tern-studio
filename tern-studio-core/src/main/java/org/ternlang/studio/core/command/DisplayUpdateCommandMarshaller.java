package org.ternlang.studio.core.command;

public class DisplayUpdateCommandMarshaller extends ObjectCommandMarshaller<DisplayUpdateCommand>{
   
   public DisplayUpdateCommandMarshaller() {
      super(CommandType.DISPLAY_UPDATE);
   }
}