package tern.studio.service.command;

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