package org.ternlang.studio.agent;

import org.ternlang.agent.message.common.ProgramArgumentArray;
import org.ternlang.agent.message.event.ExitEvent;
import org.ternlang.core.ResourceManager;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.core.ExecuteStatus;
import org.ternlang.studio.agent.debug.BreakpointConverter;
import org.ternlang.studio.agent.debug.BreakpointMap;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.task.ProcessExecutor;
import org.ternlang.studio.agent.worker.store.WorkerStore;

public class ProcessClient {
   
   private final BreakpointMap breakpoints;
   private final ProcessEventChannel client;
   private final ProcessExecutor executor;
   private final ProcessContext context;
   private final String process;
   
   public ProcessClient(ProcessContext context, ProcessEventChannel client, ProcessExecutor executor, String process) {
      this.breakpoints = new BreakpointMap();
      this.executor = executor;
      this.client = client;
      this.context = context;
      this.process = process;
   }

   public String loadScript(String project, String resource) {
      ResourceManager manager = context.getManager();
      String path = WorkerStore.getPath(project, resource);

      return manager.getString(path);
   }
   
   public void createBreakpoint(String resource, int line) {
      BreakpointMatcher matcher = context.getMatcher();
      
      breakpoints.add(resource, line);
      matcher.update(BreakpointConverter.convert(breakpoints));
   }
   
   public void removeBreakpoint(String resource, int line){
      BreakpointMatcher matcher = context.getMatcher();

      breakpoints.remove(resource, line);
      matcher.update(BreakpointConverter.convert(breakpoints));
   }
   
   public void beginExecute(String project, String resource, String dependencies, ProgramArgumentArray arguments, boolean debug) {
      BreakpointMatcher matcher = context.getMatcher();
      ProcessStore store = context.getStore();

      matcher.update(BreakpointConverter.convert(breakpoints));
      store.update(project); 
      executor.beginExecute(client, project, resource, dependencies, arguments, debug);
   }
   
   public void attachProcess(String project, String resource) {
      BreakpointMatcher matcher = context.getMatcher();
      ProcessStore store = context.getStore();

      matcher.update(BreakpointConverter.convert(breakpoints));
      store.update(project); 
      executor.attachProcess(client, project, resource);
      matcher.suspend();
   }

   public boolean waitUntilFinish(long time) {
      ExecuteLatch latch = context.getLatch();

      try {
         latch.wait(ExecuteStatus.FINISHED, time);
      }catch(Exception e) {
         return false;
      }
      return true;
   }
   
   public boolean detachClient() {
      try {
         ExecuteLatch latch = context.getLatch();
         ExitEvent event =  client.begin()
              .exit()
              .duration(0)
              .mode(org.ternlang.agent.message.common.ProcessMode.resolve(context.getMode().name()));
         
         latch.disconnect();
         client.send();
      }catch(Exception e) {
         return false;
      }         
      try {      
         client.close("Client detach");
      }catch(Exception e) {
         return false;
      } 
      return true;
   }
}