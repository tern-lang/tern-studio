package org.ternlang.studio.service.command;

public class AlertCommandMarshaller extends ObjectCommandMarshaller<AlertCommand>{
   
   public AlertCommandMarshaller() {
      super(CommandType.ALERT);
   }
}