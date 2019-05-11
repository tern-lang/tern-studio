package org.ternlang.studio.service.command;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.http.socket.FrameChannel;
import org.ternlang.studio.project.Project;

@Slf4j
public class CommandSession {

   private final Set<CommandClient> clients;
   private final CommandFilter filter;

   public CommandSession(String session) {
      this.clients = new CopyOnWriteArraySet<CommandClient>();
      this.filter = new CommandFilter(session);
   }

   public CommandFilter getFilter() {
      return filter;
   }

   public CommandClient createClient(FrameChannel channel, Project project) {
      CommandClient client = new CommandClient(channel, filter, project);
      clients.add(client);
      return client;
   }

   public void sendOpenAlert(OpenCommand command) {
      distributeCommand(command);
   }

   public void sendFolderExpandAlert(FolderExpandCommand command) {
      distributeCommand(command);
   }
   public void sendFolderCollapseAlert(FolderCollapseCommand command) {
      distributeCommand(command);
   }

   private void distributeCommand(Command command) {
      for (CommandClient client : clients) {
         try {
            client.sendCommand(command);
         } catch (Exception e) {
            log.info("Could not send command", e);
            clients.remove(client);
         }
      }
   }

}
