package org.ternlang.studio.service.command;

public class PrintErrorCommandMarshaller implements CommandMarshaller<PrintErrorCommand>{

   @Override
   public PrintErrorCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String message = value.substring(offset + 1);
      int next = message.indexOf(':');
      String process = message.substring(0, next);
      String text = message.substring(next + 1);
      
      return new PrintErrorCommand(process, text);
   }

   @Override
   public String fromCommand(PrintErrorCommand command) {
      String process = command.getProcess();
      String text = command.getText();
 
      return CommandType.PRINT_ERROR + ":" + process + ":" + text;
   }

}