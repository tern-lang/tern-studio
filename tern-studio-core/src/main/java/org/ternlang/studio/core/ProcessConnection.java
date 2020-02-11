package org.ternlang.studio.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ternlang.agent.message.common.BreakpointArray;
import org.ternlang.agent.message.common.StepType;
import org.ternlang.agent.message.common.VariablePathArrayBuilder;
import org.ternlang.agent.message.event.BreakpointsEventBuilder;
import org.ternlang.agent.message.event.ExecuteEventBuilder;
import org.ternlang.studio.agent.debug.BreakpointConverter;
import org.ternlang.studio.agent.debug.BreakpointMap;
import org.ternlang.studio.agent.debug.ProgramArgumentConverter;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
         ExecuteEventBuilder builder = channel.begin()
            .execute()
            .process(process)
            .project(projectName)
            .resource(path)
            .dependencies(dependencies)
            .debug(debug);

         builder.arguments().add(ProgramArgumentConverter.convert(arguments));
         builder.breakpoints().add(convert(projectName, breakpoints));

         return channel.send();
      } catch (Exception e) {
         log.info(process + ": Error occured sending execute event", e);
         close(process + ": Error occured sending execute event: " + e);
         throw new IllegalStateException("Could not execute script '" + resource + "' for '" + process + "'", e);
      }
   }

   public boolean suspend(String projectName, Map<String, Map<Integer, Boolean>> breakpoints) {
      try {
         BreakpointsEventBuilder builder = channel.begin()
            .breakpoints()
            .process(process);

         builder.breakpoints().add(convert(projectName, breakpoints));

         return channel.send();
      } catch (Exception e) {
         log.info(process + ": Error occured sending suspend event", e);
         close(process + ": Error occured sending suspend event: " + e);
         throw new IllegalStateException("Could not set breakpoints '" + breakpoints + "' for '" + process + "'", e);
      }
   }

   public boolean browse(String thread, Set<String> expand) {
      try {
         VariablePathArrayBuilder builder = channel.begin()
            .browse()
            .process(process)
            .thread(thread)
            .expand();

         for(String path : expand) {
            builder.add().path(path);
         }
         return channel.send();
      } catch (Exception e) {
         log.info(process + ": Error occured sending browse event", e);
         close(process + ": Error occured sending browse event: " + e);
         throw new IllegalStateException("Could not browse '" + thread + "' for '" + process + "'", e);
      }
   }

   public boolean evaluate(String thread, String expression, boolean refresh, Set<String> expand) {
      try {
         VariablePathArrayBuilder builder = channel.begin()
            .evaluate()
            .process(process)
            .thread(thread)
            .expression(expression)
            .refresh(refresh)
            .expand();

         for(String path : expand) {
            builder.add().path(path);
         }
         return channel.send();
      } catch (Exception e) {
         log.info(process + ": Error occured sending evaluate event", e);
         close(process + ": Error occured sending evaluate event: " + e);
         throw new IllegalStateException("Could not evaluate '" + expression + "' on '" + thread + "' for '" + process + "'", e);
      }
   }

   public boolean step(String thread, StepType type) {
      try {
         channel.begin()
            .step()
            .process(process)
            .thread(thread)
            .type(type);

         return channel.send();
      } catch (Exception e) {
         log.info(process + ": Error occured sending step event", e);
         close(process + ": Error occured sending step event: " + e);
         throw new IllegalStateException("Could not resume script thread '" + thread + "' for '" + process + "'", e);
      }
   }

   public boolean ping(long time) {
      try {
         channel.begin()
            .ping()
            .process(process)
            .time(time);

         if(channel.send()) {
            log.trace(process + ": Ping succeeded");
            return true;
         }
         log.info(process + ": Ping failed");
      } catch (Exception e) {
         e.printStackTrace();
         log.info(process + ": Error occured sending ping event", e);
         close(process + ": Error occured sending ping event: " + e);
      }
      return false;
   }

   private BreakpointArray convert(String name, Map<String, Map<Integer, Boolean>> breakpoints) {
      Project project = workspace.getByName(name);
      Set<String> breakpointPaths = breakpoints.keySet();
      BreakpointMap map = new BreakpointMap();

      for(String breakpointPath : breakpointPaths) {
         Set<Map.Entry<Integer, Boolean>> breakpointLines = breakpoints.get(breakpointPath).entrySet();
         String scriptPath = project.getScriptPath(breakpointPath);

         for(Map.Entry<Integer, Boolean> breakpointLine : breakpointLines) {
            Integer number = breakpointLine.getKey();
            Boolean value = breakpointLine.getValue();

            if(Boolean.TRUE.equals(value)) {
               map.add(scriptPath, number);
            }
         }
      }
      return BreakpointConverter.convert(map);
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