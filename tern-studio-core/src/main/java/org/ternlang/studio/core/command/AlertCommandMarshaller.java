package org.ternlang.studio.core.command;

public class AlertCommandMarshaller extends ObjectCommandMarshaller<AlertCommand>{
   
   public AlertCommandMarshaller() {
      super(CommandType.ALERT);
   }
}