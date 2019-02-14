package org.ternlang.studio.service.command;

public class EvaluateCommandMarshaller extends ObjectCommandMarshaller<EvaluateCommand> {

   public EvaluateCommandMarshaller() {
      super(CommandType.EVALUATE);
   }
}