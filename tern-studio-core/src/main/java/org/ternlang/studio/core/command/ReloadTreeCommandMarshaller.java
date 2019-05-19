package org.ternlang.studio.core.command;

public class ReloadTreeCommandMarshaller implements CommandMarshaller<ReloadTreeCommand>{

   @Override
   public ReloadTreeCommand toCommand(String text) {
      return new ReloadTreeCommand();
   }

   @Override
   public String fromCommand(ReloadTreeCommand command) {
      return CommandType.RELOAD_TREE.name();
   }

}