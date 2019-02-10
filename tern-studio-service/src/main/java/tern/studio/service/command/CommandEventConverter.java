package tern.studio.service.command;

import java.util.Set;

import lombok.AllArgsConstructor;

import tern.studio.agent.event.BeginEvent;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.ProfileEvent;
import tern.studio.agent.event.RegisterEvent;
import tern.studio.agent.event.ScopeEvent;
import tern.studio.agent.event.ScriptErrorEvent;
import tern.studio.agent.event.StatusEvent;
import tern.studio.agent.event.WriteErrorEvent;
import tern.studio.agent.event.WriteOutputEvent;
import tern.studio.agent.profiler.ProfileResult;
import tern.studio.common.TextEscaper;
import tern.studio.project.Project;

@AllArgsConstructor
public class CommandEventConverter {
   
   private final CommandFilter filter;
   private final Project project;

   public ScopeCommand convert(ScopeEvent event) throws Exception {
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return ScopeCommand.builder()
            .process(event.getProcess())
            .variables(event.getVariables().getLocal())
            .evaluation(event.getVariables().getEvaluation())
            .change(event.getVariables().getChange())
            .thread(event.getThread())
            .stack(event.getStack())
            .instruction(event.getInstruction())
            .status(event.getStatus().name())
            .source(event.getSource())
            .line(event.getLine())
            .depth(event.getDepth())
            .key(event.getKey())
            .resource(path)
            .build();
   }
   
   public PrintErrorCommand convert(WriteErrorEvent event) throws Exception { 
      byte[] array = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      
      return PrintErrorCommand.builder()
            .process(event.getProcess())
            .text(TextEscaper.escape(array, offset, length))
            .build();
   }
   
   public PrintOutputCommand convert(WriteOutputEvent event) throws Exception {  
      byte[] array = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      
      return PrintOutputCommand.builder()
            .process(event.getProcess())
            .text(TextEscaper.escape(array, offset, length))
            .build();
   }
   
   public ProblemCommand convert(ScriptErrorEvent event) throws Exception {
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      String name = project.getName();
      
      return ProblemCommand.builder()
            .project(name)
            .description(event.getDescription())
            .time(System.currentTimeMillis())
            .line(event.getLine())
            .resource(path)
            .build();
   }
   
   public BeginCommand convert(BeginEvent event) throws Exception {
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return BeginCommand.builder()
            .process(event.getProcess())
            .pid(event.getPid())
            .system(event.getSystem())
            .duration(event.getDuration())
            .status(event.getStatus())
            .debug(event.getStatus().isDebug())
            .resource(path)
            .build()
            .validate();
   }
   
   public ProfileCommand convert(ProfileEvent event) throws Exception {
      Set<ProfileResult> results = event.getResults();
      
      for(ProfileResult result : results) {
         String resource = result.getResource();
         String path = project.getRealPath(resource);
         
         result.setResource(path);
      }
      return ProfileCommand.builder()
            .process(event.getProcess())
            .results(results)
            .build();
   }
   
   public StatusCommand convert(RegisterEvent event) throws Exception {        
      String focus = filter.getFocus();
      String process = event.getProcess();
      
      return StatusCommand.builder()
            .process(process)
            .system(event.getSystem())
            .pid(event.getPid())
            .project(null)
            .resource(null)
            .time(System.currentTimeMillis())
            .status(event.getStatus())
            .running(event.getStatus().isRunning())
            .debug(event.getStatus().isDebug())
            .focus(process.equals(focus))
            .build()
            .validate();
   }
   
   public StatusCommand convert(StatusEvent event) throws Exception { 
      String focus = filter.getFocus();
      String process = event.getProcess();
      String resource = event.getResource();
      String path = project.getRealPath(resource);
      
      return StatusCommand.builder()
            .process(process)
            .pid(event.getPid())
            .system(event.getSystem())
            .project(event.getProject())
            .time(System.currentTimeMillis())
            .totalMemory(event.getTotalMemory())
            .usedMemory(event.getUsedMemory())
            .threads(event.getThreads())
            .status(event.getStatus())
            .running(event.getStatus().isRunning())
            .debug(event.getStatus().isDebug())
            .focus(process.equals(focus))
            .resource(path)
            .build()
            .validate();
   }
   
   public ExitCommand convert(ExitEvent event) throws Exception {  
      return ExitCommand.builder()
            .process(event.getProcess())
            .duration(event.getDuration())
            .build();
   }
   
}