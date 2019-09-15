package org.ternlang.studio.agent;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ternlang.core.trace.TraceInterceptor;
import org.ternlang.studio.agent.client.ConnectionChecker;
import org.ternlang.studio.agent.core.ExecuteData;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.ResumeType;
import org.ternlang.studio.agent.debug.SuspendController;
import org.ternlang.studio.agent.event.BreakpointsEvent;
import org.ternlang.studio.agent.event.BrowseEvent;
import org.ternlang.studio.agent.event.EvaluateEvent;
import org.ternlang.studio.agent.event.ExecuteEvent;
import org.ternlang.studio.agent.event.PingEvent;
import org.ternlang.studio.agent.event.ProcessEventAdapter;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.StepEvent;
import org.ternlang.studio.agent.limit.TimeLimiter;
import org.ternlang.studio.agent.task.ProcessExecutor;

public class ProcessAgentController extends ProcessEventAdapter {
   
   private final ProcessExecutor executor;
   private final ConnectionChecker checker;
   private final ProcessContext context;
   private final long timeout;
   
   public ProcessAgentController(ProcessContext context, ConnectionChecker checker, ProcessExecutor executor, long timeout) throws Exception {
      this.executor = executor;
      this.checker = checker;
      this.context = context;
      this.timeout = timeout;
   }

   @Override
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {
      ExecuteData data = event.getData();
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      List<String> arguments = event.getArguments();
      TimeLimiter limiter = context.getTimeLimiter();
      BreakpointMatcher matcher = context.getMatcher();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessStore store = context.getStore();
      String actual = context.getProcess();
      String dependencies = data.getDependencies();
      String target = data.getProcess();
      String project = data.getProject();
      String resource = data.getResource();
      boolean debug = data.isDebug();
      
      if(!target.equals(actual)) {
         throw new IllegalArgumentException("Process '" +actual+ "' received event for '"+target+"'");
      }
      if(!data.isDebug()) {
         interceptor.clear(); // disable interceptors
      }
      interceptor.register(limiter); // make sure time limit applies
      limiter.expireAfter(timeout); // expire after timeout milliseconds
      matcher.update(breakpoints);
      store.update(project); 
      executor.beginExecute(channel, project, resource, dependencies, arguments, debug);
   }
   
   @Override
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      BreakpointMatcher matcher = context.getMatcher();
      matcher.update(breakpoints);
   }
   
   @Override
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      int type = event.getType();
      
      if(type == StepEvent.RUN) {
         controller.resume(ResumeType.RUN, thread);
      } else if(type == StepEvent.STEP_IN) {
         controller.resume(ResumeType.STEP_IN, thread);
      } else if(type == StepEvent.STEP_OUT) {
         controller.resume(ResumeType.STEP_OUT, thread);
      } else if(type == StepEvent.STEP_OVER) {
         controller.resume(ResumeType.STEP_OVER, thread);
      }
   }
   
   @Override
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      Set<String> expand = event.getExpand();
      
      controller.browse(expand, thread);
   }
   
   @Override
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      String expression = event.getExpression();
      Set<String> expand = event.getExpand();
      boolean refresh = event.isRefresh();
      
      controller.evaluate(expand, thread, expression, refresh);
   }

   @Override
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {
      checker.update(channel, event);
   }

   @Override
   public void onClose(ProcessEventChannel channel) throws Exception {
      ProcessMode mode = context.getMode();
      
      if(mode.isTerminateRequired()) {
         TerminateHandler.terminate("Close event received");
      } else {
         checker.close(); // disconnect straight away
      }
   }
}