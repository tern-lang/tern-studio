package tern.studio.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tern.core.ResourceManager;
import tern.studio.agent.core.ExecuteLatch;
import tern.studio.agent.core.ExecuteStatus;
import tern.studio.agent.debug.BreakpointMatcher;
import tern.studio.agent.event.ExitEvent;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.task.ProcessExecutor;
import tern.studio.agent.worker.store.WorkerStore;

public class ProcessClient {
   
   private final Map<String, Map<Integer, Boolean>> breakpoints;
   private final ProcessEventChannel client;
   private final ProcessExecutor executor;
   private final ProcessContext context;
   private final String process;
   
   public ProcessClient(ProcessContext context, ProcessEventChannel client, ProcessExecutor executor, String process) {
      this.breakpoints = new HashMap<String, Map<Integer, Boolean>>();
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
      Map<Integer, Boolean> lines = breakpoints.get(resource);
      BreakpointMatcher matcher = context.getMatcher();
      
      if(lines == null) {
         lines = new HashMap<Integer, Boolean>();
         breakpoints.put(resource, lines);
      }
      lines.put(line, Boolean.TRUE);
      matcher.update(breakpoints);
   }
   
   public void removeBreakpoint(String resource, int line){
      Map<Integer, Boolean> lines = breakpoints.get(resource);
      BreakpointMatcher matcher = context.getMatcher();
      
      if(lines == null) {
         lines = new HashMap<Integer, Boolean>();
         breakpoints.put(resource, lines);
      }
      lines.put(line, Boolean.FALSE);
      matcher.update(breakpoints);
   }
   
   public void beginExecute(String project, String resource, String dependencies, List<String> arguments, boolean debug) {
      BreakpointMatcher matcher = context.getMatcher();
      ProcessStore store = context.getStore();

      matcher.update(breakpoints);
      store.update(project); 
      executor.beginExecute(client, project, resource, dependencies, arguments, debug);
   }
   
   public void attachProcess(String project, String resource) {
      BreakpointMatcher matcher = context.getMatcher();
      ProcessStore store = context.getStore();

      matcher.update(breakpoints);
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
      ExitEvent event = new ExitEvent.Builder(process)
         .withDuration(0)
         .withMode(context.getMode())
         .build();   
      
      try {     
         ExecuteLatch latch = context.getLatch();
         
         latch.disconnect();
         client.send(event);
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