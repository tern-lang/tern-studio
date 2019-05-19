package org.ternlang.studio.core.command;

public class StatusCommandMarshaller extends ObjectCommandMarshaller<StatusCommand>{
   
   public StatusCommandMarshaller() {
      super(CommandType.STATUS);
   }
}