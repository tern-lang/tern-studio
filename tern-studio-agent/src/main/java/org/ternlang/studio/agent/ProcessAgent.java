package org.ternlang.studio.agent;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.ternlang.core.scope.EmptyModel;
import org.ternlang.core.scope.Model;
import org.ternlang.core.trace.TraceInterceptor;
import org.ternlang.studio.agent.client.ConnectTunnelClient;
import org.ternlang.studio.agent.client.ConnectionChecker;
import org.ternlang.studio.agent.client.ConnectionListener;
import org.ternlang.studio.agent.core.CompileValidator;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.core.ExecuteState;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.FaultContextExtractor;
import org.ternlang.studio.agent.debug.ResumeType;
import org.ternlang.studio.agent.debug.SuspendController;
import org.ternlang.studio.agent.debug.SuspendInterceptor;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ProcessEventTimer;
import org.ternlang.studio.agent.event.RegisterEvent;
import org.ternlang.studio.agent.limit.TimeLimitListener;
import org.ternlang.studio.agent.log.AsyncLog;
import org.ternlang.studio.agent.log.ConsoleLog;
import org.ternlang.studio.agent.log.Log;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.log.LogLogger;
import org.ternlang.studio.agent.log.TraceLogger;
import org.ternlang.studio.agent.profiler.TraceProfiler;
import org.ternlang.studio.agent.task.ProcessExecutor;

public class ProcessAgent {

   private final ProcessContext context;
   private final LogLevel level;
   private final Model model;
   private final Log log;
   private final long limit;

   public ProcessAgent(ProcessContext context, LogLevel level) {
      this(context, level, TimeUnit.DAYS.toMillis(5));
   }
   
   public ProcessAgent(ProcessContext context, LogLevel level, long limit) {
      this.model = new EmptyModel();
      this.log = new ConsoleLog();
      this.context = context;
      this.level = level;
      this.limit = limit;
   }
   
   public ProcessClient start(final URI root, final Runnable task) throws Exception {
      return start(root, task, model);
   }
   
   public ProcessClient start(final URI root, final Runnable task, final Model model) throws Exception {
      return start(root, task, model, log);
   }
   
   public ProcessClient start(final URI root, final Runnable task, final Model model, final Log log) throws Exception {
      final BreakpointMatcher matcher = context.getMatcher();
      final SuspendController controller = context.getController();
      final TraceInterceptor interceptor = context.getInterceptor();
      final TraceProfiler profiler = context.getProfiler();
      final String process = context.getProcess();
      final ProcessMode mode = context.getMode();
      final ExecuteLatch latch = context.getLatch();
      final ExecuteState state = latch.getState();
      final String system = state.getSystem();
      final String pid = state.getPid();
      final String host = root.getHost();
      final int port = root.getPort();
      
      try {
         final AsyncLog adapter = new AsyncLog(log, level);
         final TimeLimitListener limiter = new TimeLimitListener(limit);
         final TraceLogger logger = new LogLogger(adapter, level);
         final CompileValidator validator = new CompileValidator(context);
         final ConnectionChecker checker = new ConnectionChecker(context, process);
         final ProcessExecutor executor = new ProcessExecutor(context, checker, logger, mode, model);
         final ProcessAgentController listener = new ProcessAgentController(context, checker, executor);
         final ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         final ConnectTunnelClient client = new ConnectTunnelClient(timer, checker, logger);
         final ProcessEventChannel channel = client.connect(process, host, port);
         final SuspendInterceptor suspender = new SuspendInterceptor(channel, matcher, controller, mode, process);
         final FaultContextExtractor extractor = new FaultContextExtractor(channel, logger, process);
         final RegisterEvent register = new RegisterEvent.Builder(process)
            .withPid(pid)
            .withSystem(system)
            .build();
         
         interceptor.register(limiter);
         interceptor.register(profiler);
         interceptor.register(suspender);
         interceptor.register(extractor);
         channel.send(register); // send the initial register event
         validator.validate();
         checker.register(new ConnectionListener() {
           
            @Override
            public void onClose(){
               try {
                  adapter.stop();
                  checker.close();
                  controller.resume(ResumeType.RUN);
                  interceptor.remove(profiler);
                  interceptor.remove(suspender);
                  interceptor.remove(extractor);
                  controller.resume(ResumeType.RUN);
                  channel.close("Stop requested");
               }catch(Exception e) {
                  logger.info("Error stopping client", e);
               } finally {
                  try {
                     task.run();
                  }catch(Exception e){
                     logger.info("Error executing completion task", e);
                  }
               }
            }
         });
         checker.start();
         
         return new ProcessClient(context, channel, executor, process);
      } catch (Exception e) {
         throw new IllegalStateException("Could not start process '" + process+ "'", e);
      }
   }
}