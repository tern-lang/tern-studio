package org.ternlang.studio.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.annotation.Value;
import org.simpleframework.transport.Channel;
import org.ternlang.agent.message.event.StepEvent;
import org.ternlang.studio.agent.event.ProcessEventFilter;
import org.ternlang.studio.agent.event.ProcessEventListener;
import org.ternlang.studio.core.command.AttachCommand;
import org.ternlang.studio.core.command.BreakpointsCommand;
import org.ternlang.studio.core.command.BrowseCommand;
import org.ternlang.studio.core.command.EvaluateCommand;
import org.ternlang.studio.core.command.ExecuteCommand;
import org.ternlang.studio.core.command.RemoteDebugCommand;
import org.ternlang.studio.core.command.StepCommand;
import org.ternlang.studio.core.command.StepCommand.StepType;
import org.ternlang.studio.project.ClassPathFile;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.config.ProcessConfiguration;
import org.ternlang.studio.project.config.ProcessConfigurationLoader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProcessManager implements ProcessRemoteController {
   
   private final Map<String, ProcessConnection> connections; // active processes
   private final ProcessConfiguration configuration;
   private final ProcessConfigurationLoader loader;
   private final ProcessPool pool;
   private final Workspace workspace;

   public ProcessManager(ProcessConfigurationLoader loader, ProcessConfiguration configuration, ProcessLauncher launcher, ProcessNameFilter filter, Workspace workspace, @Value("${agent-pool}") int capacity) throws Exception {
      this.connections = new ConcurrentHashMap<String, ProcessConnection>();
      this.pool = new ProcessPool(configuration, launcher, filter, workspace, capacity);
      this.configuration = configuration;
      this.workspace = workspace;
      this.loader = loader;
   }
   
   public void connect(ProcessEventListener listener, Channel channel, String process) {
      pool.register(listener);
      pool.connect(channel, process);
   }
   
   public void register(ProcessEventListener listener) {
      pool.register(listener);
   }
   
   public void remove(ProcessEventListener listener) {
      pool.remove(listener);
   }
   
   public boolean execute(ExecuteCommand command, ProcessEventFilter filter) { 
      String focus = filter.getFocus();
      String agent = pool.active(focus) ? null : focus; // if not running then use
      ProcessConnection connection = pool.acquire(agent);
      
      if(connection != null) {
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         List<String> arguments = command.getArguments();
         String projectName = command.getProject();
         Project project = workspace.getByName(projectName);
         ClassPathFile classPath = project.getClassPath();
         String dependencies = classPath.getPath();
         String resource = command.getResource();
         String process = connection.toString();
         boolean debug = command.isDebug();
         
         if(filter != null) {
            filter.setFocus(process);
         }
         connections.put(process, connection);
         
         return connection.execute(projectName, resource, dependencies, breakpoints, arguments, debug);
      }
      return true;
   }
   
   public boolean debug(RemoteDebugCommand command, String process) { // attach remote debug
      ProcessConnection connection = pool.acquire(process);
      
      if(connection != null) {
         connections.put(process, connection);
         return true;
      }
      return false;
   }
   
   public boolean breakpoints(BreakpointsCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String project = command.getProject();
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         return connection.suspend(project, breakpoints);
      }
      return true;
   }
   
   public boolean attach(AttachCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String project = command.getProject();
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         return connection.suspend(project, breakpoints);
      }
      return true;
   }
   
   public boolean browse(BrowseCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         Set<String> expand = command.getExpand();
         String thread = command.getThread();
         return connection.browse(thread, expand);
      }
      return true;
   }
   
   public boolean evaluate(EvaluateCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         Set<String> expand = command.getExpand();
         String expression = command.getExpression();
         String thread = command.getThread();
         boolean refresh = command.isRefresh();
         return connection.evaluate(thread, expression, refresh, expand);
      }
      return true;
   }
   
   public boolean step(StepCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String thread = command.getThread();
         StepType type = command.getType();
         
         if(type == StepType.RUN) {
            return connection.step(thread, org.ternlang.agent.message.common.StepType.RUN);
         } else if(type == StepType.STEP_IN) {
            return connection.step(thread, org.ternlang.agent.message.common.StepType.STEP_IN);
         } else if(type == StepType.STEP_OUT) {
            return connection.step(thread, org.ternlang.agent.message.common.StepType.STEP_OUT);
         } else if(type == StepType.STEP_OVER) {
            return connection.step(thread, org.ternlang.agent.message.common.StepType.STEP_OVER);
         }
      }
      return true;
   }
   
   @Override
   public boolean start(String process) { // move from waiting to running, used by agent
      if(!connections.containsKey(process)) {
         ProcessConnection connection = pool.acquire(process);
         
         if(connection != null) {
            connections.put(process, connection);
            return true;
         }
         return false;
      }
      return true; // already started
   }
   
   @Override
   public boolean stop(String process) {
      ProcessConnection connection = connections.remove(process);
      
      if(connection != null) {
         connection.close(process + ": Explicit stop requested");
      }
      return true;
   }

   @Override
   public boolean detach(String process) {
      ProcessConnection connection = connections.remove(process);

      if(connection != null) {
         log.debug(process + ": Detach requested");
      }
      return true;
   }
   
   @Override
   public boolean ping(String process, long time) {
      ProcessConnection connection = connections.get(process);

      if(connection != null) {
         return connection.ping(time);
      } 
      return pool.ping(process, time); // the process might not be active
   }
   
   public void start(String host, int port) {
      loader.load(configuration);
      configuration.setHost(host);
      configuration.setPort(port);
      pool.start(host, port);
   }
   
   public void launch() {
      pool.launch();
   }

}