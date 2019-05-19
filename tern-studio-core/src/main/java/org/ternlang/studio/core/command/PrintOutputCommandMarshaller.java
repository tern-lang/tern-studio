package org.ternlang.studio.core.command;

public class PrintOutputCommandMarshaller implements CommandMarshaller<PrintOutputCommand> {

   @Override
   public PrintOutputCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String message = value.substring(offset + 1);
      int next = message.indexOf(':');
      String process = message.substring(0, next);
      String text = message.substring(next + 1);
      
      return new PrintOutputCommand(process, text);
   }

   @Override
   public String fromCommand(PrintOutputCommand command) {
      String process = command.getProcess();
      String text = command.getText();
      
      return CommandType.PRINT_OUTPUT + ":" + process + ":" + text;
   }

}