package tern.studio.service.command;

import org.simpleframework.http.socket.FrameChannel;
import tern.studio.project.Project;

public class CommandClient {
   
   private final CommandWriter writer;
   private final FrameChannel channel;
   private final Project project;
   
   public CommandClient(FrameChannel channel, Project project) {
      this.writer = new CommandWriter();
      this.channel = channel;
      this.project = project;
   } 
   
   public void sendCommand(Command command) throws Exception {
      String message = writer.write(command);
      channel.send(message);
   }
   
   public void sendDependencyError(String resource, String description, long time, int line) throws Exception {
      String name = project.getName();
      ProblemCommand command = ProblemCommand.builder()
            .project(name)
            .description(description)
            .resource(resource)
            .time(time)
            .line(line)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendScriptError(String resource, String description, long time, int line) throws Exception {
      String name = project.getName();
      ProblemCommand command = ProblemCommand.builder()
            .project(name)
            .description(description)
            .resource(resource)
            .time(time)
            .line(line)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendProcessTerminate(String process) throws Exception {
      TerminateCommand command = TerminateCommand.builder()
            .process(process)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendAlert(String resource, String text) throws Exception {
      AlertCommand command = AlertCommand.builder()
            .resource(resource)
            .message(text)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendReloadTree() throws Exception {
      ReloadTreeCommand command = ReloadTreeCommand.builder()
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
}