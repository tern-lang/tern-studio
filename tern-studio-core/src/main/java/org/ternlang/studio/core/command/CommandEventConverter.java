package org.ternlang.studio.core.command;

import lombok.AllArgsConstructor;
import org.ternlang.agent.message.event.BeginEvent;
import org.ternlang.agent.message.event.ExitEvent;
import org.ternlang.agent.message.event.ProfileEvent;
import org.ternlang.agent.message.event.RegisterEvent;
import org.ternlang.agent.message.event.ScopeEvent;
import org.ternlang.agent.message.event.ScriptErrorEvent;
import org.ternlang.agent.message.event.StatusEvent;
import org.ternlang.agent.message.event.WriteErrorEvent;
import org.ternlang.agent.message.event.WriteOutputEvent;
import org.ternlang.studio.agent.core.ExecuteStatus;
import org.ternlang.studio.agent.debug.ScopeVariableConverter;
import org.ternlang.studio.agent.debug.ScopeVariableTree;
import org.ternlang.studio.agent.profiler.ProfileResult;
import org.ternlang.studio.agent.profiler.ProfileResultConverter;
import org.ternlang.studio.common.TextEscaper;
import org.ternlang.studio.project.Project;

import java.util.Set;

@AllArgsConstructor
public class CommandEventConverter {
   
   private final CommandFilter filter;
   private final Project project;

   public ScopeCommand convert(ScopeEvent event) throws Exception {
      String resource = event.resource().toString();
      String path = project.getRealPath(resource);
      ScopeVariableTree variables = ScopeVariableConverter.convert(event.variables());

      return ScopeCommand.builder()
            .process(event.process().toString())
            .variables(variables.getLocal())
            .evaluation(variables.getEvaluation())
            .change(variables.getChange())
            .thread(event.thread().toString())
            .stack(event.stack().toString())
            .instruction(event.instruction().toString())
            .status(event.status().name())
            .source(event.source().toString())
            .line(event.line())
            .depth(event.depth())
            .key(event.key())
            .resource(path)
            .build();
   }
   
   public PrintErrorCommand convert(WriteErrorEvent event) throws Exception {
      int length = event.length();
      byte[] array = new byte[length];

      event.data().get(0, array);
      
      return PrintErrorCommand.builder()
            .process(event.process().toString())
            .text(TextEscaper.escape(array, 0, length))
            .build();
   }
   
   public PrintOutputCommand convert(WriteOutputEvent event) throws Exception {
      int length = event.length();
      byte[] array = new byte[length];

      event.data().get(0, array);
      
      return PrintOutputCommand.builder()
            .process(event.process().toString())
            .text(TextEscaper.escape(array, 0, length))
            .build();
   }
   
   public ProblemCommand convert(ScriptErrorEvent event) throws Exception {
      String resource = event.resource().toString();
      String path = project.getRealPath(resource);
      String name = project.getName();
      
      return ProblemCommand.builder()
            .project(name)
            .description(event.description().toString())
            .message(event.message().toString())
            .time(System.currentTimeMillis())
            .line(event.line())
            .resource(path)
            .build();
   }
   
   public BeginCommand convert(BeginEvent event) throws Exception {
      String resource = event.resource().toString();
      String path = project.getRealPath(resource);
      
      return BeginCommand.builder()
            .process(event.process().toString())
            .pid(event.pid().toString())
            .system(event.system().toString())
            .duration(event.duration())
            .status(org.ternlang.studio.agent.core.ExecuteStatus.resolveStatus(event.status().name()))
            .debug(org.ternlang.studio.agent.core.ExecuteStatus.resolveStatus(event.status().name()).isDebug())
            .resource(path)
            .build()
            .validate();
   }
   
   public ProfileCommand convert(ProfileEvent event) throws Exception {
      Set<ProfileResult> results = ProfileResultConverter.convert(event.results());
      
      for(ProfileResult result : results) {
         String resource = result.getResource();
         String path = project.getRealPath(resource);
         
         result.setResource(path);
      }
      return ProfileCommand.builder()
            .process(event.process().toString())
            .results(results)
            .build();
   }
   
   public StatusCommand convert(RegisterEvent event) throws Exception {        
      String focus = filter.getFocus();
      String process = event.process().toString();
      
      return StatusCommand.builder()
            .process(process)
            .system(event.system().toString())
            .pid(event.pid().toString())
            .project(null)
            .resource(null)
            .time(System.currentTimeMillis())
            .status(ExecuteStatus.REGISTERING)
            .running(ExecuteStatus.REGISTERING.isRunning())
            .debug(ExecuteStatus.REGISTERING.isDebug())
            .focus(process.equals(focus))
            .build()
            .validate();
   }
   
   public StatusCommand convert(StatusEvent event) throws Exception {
      String focus = filter.getFocus();
      String process = event.process().toString();
      String resource = event.resource().toString();
      String path = project.getRealPath(resource);
      
      return StatusCommand.builder()
            .process(process)
            .pid(event.pid().toString())
            .system(event.system().toString())
            .project(event.project().toString())
            .time(System.currentTimeMillis())
            .totalTime(event.totalTime())
            .usedTime(event.usedTime())
            .totalMemory(event.totalMemory())
            .usedMemory(event.usedMemory())
            .threads(event.threads())
            .status(org.ternlang.studio.agent.core.ExecuteStatus.resolveStatus(event.status().name()))
            .running(org.ternlang.studio.agent.core.ExecuteStatus.resolveStatus(event.status().name()).isRunning())
            .debug(org.ternlang.studio.agent.core.ExecuteStatus.resolveStatus(event.status().name()).isDebug())
            .focus(process.equals(focus))
            .resource(path)
            .build()
            .validate();
   }
   
   public ExitCommand convert(ExitEvent event) throws Exception {  
      return ExitCommand.builder()
            .process(event.process().toString())
            .duration(event.duration())
            .build();
   }
   
}