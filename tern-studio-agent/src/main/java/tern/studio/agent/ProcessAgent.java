package tern.studio.agent;

import java.net.URI;

import tern.core.scope.EmptyModel;
import tern.core.scope.Model;
import tern.core.trace.TraceInterceptor;
import tern.studio.agent.client.ConnectTunnelClient;
import tern.studio.agent.client.ConnectionChecker;
import tern.studio.agent.client.ConnectionListener;
import tern.studio.agent.core.CompileValidator;
import tern.studio.agent.core.ExecuteLatch;
import tern.studio.agent.core.ExecuteState;
import tern.studio.agent.debug.BreakpointMatcher;
import tern.studio.agent.debug.FaultContextExtractor;
import tern.studio.agent.debug.ResumeType;
import tern.studio.agent.debug.SuspendController;
import tern.studio.agent.debug.SuspendInterceptor;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.event.ProcessEventTimer;
import tern.studio.agent.event.RegisterEvent;
import tern.studio.agent.log.AsyncLog;
import tern.studio.agent.log.ConsoleLog;
import tern.studio.agent.log.Log;
import tern.studio.agent.log.LogLevel;
import tern.studio.agent.log.LogLogger;
import tern.studio.agent.log.TraceLogger;
import tern.studio.agent.profiler.TraceProfiler;
import tern.studio.agent.task.ProcessExecutor;

public class ProcessAgent {

   private final ProcessContext context;
   private final LogLevel level;
   private final Model model;
   private final Log log;

   public ProcessAgent(ProcessContext context, LogLevel level) {
      this.model = new EmptyModel();
      this.log = new ConsoleLog();
      this.context = context;
      this.level = level;
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