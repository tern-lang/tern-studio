package org.ternlang.studio.service.command;

public class StatusCommandMarshaller extends ObjectCommandMarshaller<StatusCommand>{
   
   public StatusCommandMarshaller() {
      super(CommandType.STATUS);
   }
}