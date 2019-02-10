package tern.studio.service.command;

import java.util.Map;

import tern.studio.agent.debug.ScopeVariableTree;
import tern.studio.agent.event.BeginEvent;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.FaultEvent;
import tern.studio.agent.event.PongEvent;
import tern.studio.agent.event.ProcessEventAdapter;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProfileEvent;
import tern.studio.agent.event.RegisterEvent;
import tern.studio.agent.event.ScopeEvent;
import tern.studio.agent.event.ScriptErrorEvent;
import tern.studio.agent.event.StatusEvent;
import tern.studio.agent.event.WriteErrorEvent;
import tern.studio.agent.event.WriteOutputEvent;
import tern.studio.project.Project;
import tern.studio.service.FaultLogger;

public class CommandEventForwarder extends ProcessEventAdapter {
   
   private final CommandEventConverter converter;
   private final CommandFilter filter;
   private final CommandClient client;
   private final FaultLogger logger;
   
   public CommandEventForwarder(CommandClient client, CommandFilter filter, Project project) {
      this.converter = new CommandEventConverter(filter, project);
      this.logger = new FaultLogger();
      this.filter = filter;
      this.client = client;
   } 
   
   @Override
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
      String source = event.getSource();
      String process = event.getProcess();
      
      if(filter.isFocused(event)) {
         ScopeCommand command = converter.convert(event);
         client.sendCommand(command);
      } else {
         if(source != null) {
            ScopeCommand command = converter.convert(event);
            filter.setFocus(process);
            client.sendCommand(command);            
         }
      }
   }
   
   @Override
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
      if(filter.isFocused(event)) {
         ScopeVariableTree tree = event.getVariables();
         Map<String, Map<String, String>> local = tree.getLocal();
         String process = event.getProcess();
         String thread = event.getThread();
         String cause = event.getCause();
         String resource = event.getResource();
         int line = event.getLine();
         logger.log(process, local, resource, thread, cause, line);
      }
   }
   
   @Override
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {   
      PrintErrorCommand printcommand = converter.convert(event);
      client.sendCommand(printcommand);
   }
   
   @Override
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {  
      PrintOutputCommand printCommand = converter.convert(event);
      client.sendCommand(printCommand);
   }
   
   @Override
   public void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception {
      ProblemCommand problemCommand = converter.convert(event);
      client.sendCommand(problemCommand);
   }
   
   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
      if(filter.isFocused(event)) {
         BeginCommand beginCommand = converter.convert(event);
         StatusCommand statusCommand = converter.convert((StatusEvent)event);

         client.sendCommand(statusCommand);
         client.sendCommand(beginCommand);
      }
   }
   
   @Override
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
      if(filter.isFocused(event)) {
         ProfileCommand profileCommand = converter.convert(event);
         client.sendCommand(profileCommand);
      }
   }
   
   @Override
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {  
      StatusCommand statusCommand = converter.convert(event);
      client.sendCommand(statusCommand);
   }
   
   @Override
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {  
      StatusCommand statusCommand = converter.convert(event);
      client.sendCommand(statusCommand);
   }
   
   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {  
      ExitCommand exitCommand = converter.convert(event);
      client.sendCommand(exitCommand);
   }
   
   @Override
   public void onClose(ProcessEventChannel channel) throws Exception { 
      String focus = filter.getFocus();
      if(focus != null) {
         TerminateCommand terminateCommand = TerminateCommand.builder()
               .process(focus)
               .build();
         client.sendCommand(terminateCommand); 
      }
   }
}