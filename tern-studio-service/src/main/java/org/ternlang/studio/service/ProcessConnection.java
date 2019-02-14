package org.ternlang.studio.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.agent.event.BreakpointsEvent;
import org.ternlang.studio.agent.event.BrowseEvent;
import org.ternlang.studio.agent.event.EvaluateEvent;
import org.ternlang.studio.agent.event.ExecuteEvent;
import org.ternlang.studio.agent.event.PingEvent;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.StepEvent;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

@Slf4j
@AllArgsConstructor
public class ProcessConnection {

   private final ProcessEventChannel channel;
   private final Workspace workspace;
   private final String process;

   public boolean execute(String projectName, String resource, String dependencies, Map<String, Map<Integer, Boolean>> breakpoints, List<String> arguments, boolean debug) {
      try {
         Project project = workspace.getByName(projectName);
         String path = project.getScriptPath(resource);
         
         ExecuteEvent event = new ExecuteEvent.Builder(process)
            .withProject(projectName)
            .withResource(path)
            .withDependencies(dependencies)
            .withBreakpoints(convert(projectName, breakpoints))
            .withArguments(arguments)
            .withDebug(debug)
            .build();

         return channel.send(event);
      } catch (Exception e) {
         log.info(process + ": Error occured sending execute event", e);
         close(process + ": Error occured sending execute event: " + e);
         throw new IllegalStateException("Could not execute script '" + resource + "' for '" + process + "'", e);
      }
   }
   
   public boolean suspend(String projectName, Map<String, Map<Integer, Boolean>> breakpoints) {
      try {
         BreakpointsEvent event = new BreakpointsEvent.Builder(process)
            .withBreakpoints(convert(projectName, breakpoints))
            .build();
         
         return channel.send(event);
      } catch (Exception e) {
         log.info(process + ": Error occured sending suspend event", e);
         close(process + ": Error occured sending suspend event: " + e);
         throw new IllegalStateException("Could not set breakpoints '" + breakpoints + "' for '" + process + "'", e);
      }
   }
   
   public boolean browse(String thread, Set<String> expand) {
      try {
         BrowseEvent event = new BrowseEvent.Builder(process)
            .withThread(thread)
            .withExpand(expand)
            .build();
         
         return channel.send(event);
      } catch (Exception e) {
         log.info(process + ": Error occured sending browse event", e);
         close(process + ": Error occured sending browse event: " + e);
         throw new IllegalStateException("Could not browse '" + thread + "' for '" + process + "'", e);
      }
   }
   
   public boolean evaluate(String thread, String expression, boolean refresh, Set<String> expand) {
      try {
         EvaluateEvent event = new EvaluateEvent.Builder(process)
            .withThread(thread)
            .withExpression(expression)
            .withRefresh(refresh)
            .withExpand(expand)
            .build();
         
         return channel.send(event);
      } catch (Exception e) {
         log.info(process + ": Error occured sending evaluate event", e);
         close(process + ": Error occured sending evaluate event: " + e);
         throw new IllegalStateException("Could not evaluate '" + expression + "' on '" + thread + "' for '" + process + "'", e);
      }
   }
   
   public boolean step(String thread, int type) {
      try {
         StepEvent event = new StepEvent.Builder(process)
            .withThread(thread)
            .withType(type)
            .build();

         return channel.send(event);
      } catch (Exception e) {
         log.info(process + ": Error occured sending step event", e);
         close(process + ": Error occured sending step event: " + e);
         throw new IllegalStateException("Could not resume script thread '" + thread + "' for '" + process + "'", e);
      }
   }

   public boolean ping(long time) {
      try {
         PingEvent event = new PingEvent.Builder(process)
            .withTime(time)
            .build();
         
         if(channel.send(event)) {
            log.trace(process + ": Ping succeeded");
            return true;
         }
         log.info(process + ": Ping failed");
      } catch (Exception e) {
         log.info(process + ": Error occured sending ping event", e);
         close(process + ": Error occured sending ping event: " + e);
      }
      return false;
   }
   
   private Map<String, Map<Integer, Boolean>> convert(String name, Map<String, Map<Integer, Boolean>> breakpoints) {
      Project project = workspace.getByName(name);
      Set<String> breakpointPaths = breakpoints.keySet();
      
      if(!breakpointPaths.isEmpty()) {
         Map<String, Map<Integer, Boolean>> convertedBreakpoints = new LinkedHashMap<String, Map<Integer, Boolean>>();
      
         for(String breakpointPath : breakpointPaths) {
            Map<Integer, Boolean> breakpointLines = breakpoints.get(breakpointPath);
            String scriptPath = project.getScriptPath(breakpointPath);
            
            convertedBreakpoints.put(scriptPath, breakpointLines);
         }
         return convertedBreakpoints;
      }
      return breakpoints;
   }
   
   public void close(String reason) {
      try {
         log.info(process + ": Closing connection: " +reason);
         channel.close(process + ": Closing connection: " +reason);
      } catch (Exception e) {
         log.info(process + ": Error occured closing channel", e);
      }
   }

   public String getProcess() {
      return process;
   }
   
   @Override
   public String toString() {
      return process;
   }
}