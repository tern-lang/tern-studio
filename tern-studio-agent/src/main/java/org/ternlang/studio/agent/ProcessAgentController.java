package org.ternlang.studio.agent;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ternlang.agent.message.common.BreakpointArray;
import org.ternlang.agent.message.common.ExecuteData;
import org.ternlang.agent.message.common.ProgramArgumentArray;
import org.ternlang.agent.message.common.StepType;
import org.ternlang.agent.message.common.VariablePathArray;
import org.ternlang.agent.message.event.BreakpointsEvent;
import org.ternlang.agent.message.event.BrowseEvent;
import org.ternlang.agent.message.event.EvaluateEvent;
import org.ternlang.agent.message.event.ExecuteEvent;
import org.ternlang.agent.message.event.PingEvent;
import org.ternlang.agent.message.event.StepEvent;
import org.ternlang.core.trace.TraceInterceptor;
import org.ternlang.studio.agent.client.ConnectionChecker;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.ResumeType;
import org.ternlang.studio.agent.debug.SuspendController;
import org.ternlang.studio.agent.event.ProcessEventAdapter;
import org.ternlang.studio.agent.event.ProcessEventChannel;
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
      ExecuteData data = event.data();
      BreakpointArray breakpoints = event.breakpoints();
      ProgramArgumentArray arguments = event.arguments();
      TimeLimiter limiter = context.getTimeLimiter();
      BreakpointMatcher matcher = context.getMatcher();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessStore store = context.getStore();
      String actual = context.getProcess();
      String dependencies = data.dependencies().toString();
      String target = data.process().toString();
      String project = data.project().toString();
      String resource = data.resource().toString();
      boolean debug = data.debug();
      
      if(!target.equals(actual)) {
         throw new IllegalArgumentException("Process '" +actual+ "' received event for '"+target+"'");
      }
      if(!data.debug()) {
         interceptor.clear(); // disable interceptors
      }
      limiter.expireAfter(timeout); // expire after timeout milliseconds
      matcher.update(breakpoints);
      store.update(project); 
      executor.beginExecute(channel, project, resource, dependencies, arguments, debug);
   }
   
   @Override
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {
      BreakpointArray breakpoints = event.breakpoints();
      BreakpointMatcher matcher = context.getMatcher();
      matcher.update(breakpoints);
   }
   
   @Override
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.thread().toString();
      StepType type = event.type();
      
      if(type.isRun()) {
         controller.resume(ResumeType.RUN, thread);
      } else if(type.isStepIn()) {
         controller.resume(ResumeType.STEP_IN, thread);
      } else if(type.isStepOut()) {
         controller.resume(ResumeType.STEP_OUT, thread);
      } else if(type.isStepOver()) {
         controller.resume(ResumeType.STEP_OVER, thread);
      }
   }
   
   @Override
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.thread().toString();
      VariablePathArray expand = event.expand();
      
      controller.browse(expand, thread);
   }
   
   @Override
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.thread().toString();
      String expression = event.expression().toString();
      VariablePathArray expand = event.expand();
      boolean refresh = event.refresh();
      
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