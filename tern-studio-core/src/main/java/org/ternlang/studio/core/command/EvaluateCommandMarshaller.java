package org.ternlang.studio.core.command;

public class EvaluateCommandMarshaller extends ObjectCommandMarshaller<EvaluateCommand> {

   public EvaluateCommandMarshaller() {
      super(CommandType.EVALUATE);
   }
}