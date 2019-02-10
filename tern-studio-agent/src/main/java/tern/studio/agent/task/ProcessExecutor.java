package tern.studio.agent.task;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import tern.common.thread.ThreadBuilder;
import tern.core.scope.Model;
import tern.studio.agent.ProcessContext;
import tern.studio.agent.ProcessMode;
import tern.studio.agent.ProcessModel;
import tern.studio.agent.client.ConnectionChecker;
import tern.studio.agent.core.ClassPathUpdater;
import tern.studio.agent.core.ExecuteData;
import tern.studio.agent.core.ExecuteLatch;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.log.TraceLogger;

public class ProcessExecutor {

   private final ConnectionChecker checker;
   private final ThreadFactory factory;
   private final ProcessContext context;
   private final TraceLogger logger;
   private final ProcessMode mode;
   private final Model model;
   private final String[] empty;
   
   public ProcessExecutor(ProcessContext context, ConnectionChecker checker, TraceLogger logger, ProcessMode mode, Model model) {
      this.factory = new ThreadBuilder();
      this.empty = new String[]{};
      this.checker = checker;
      this.logger = logger;
      this.context = context;
      this.model = model;
      this.mode = mode;
   }

   public void beginExecute(ProcessEventChannel channel, String project, String resource, String dependencies, List<String> arguments, boolean debug) {
      ExecuteLatch latch = context.getLatch();
      String process = context.getProcess();
      
      try {         
         if(resource != null) {
            ProcessModel overrides = new ProcessModel(model);
            ExecuteData data = new ExecuteData(process, project, resource, dependencies, debug);
            ConsoleConnector connector = new ConsoleConnector(channel, process);
            ProcessTask harness = new ProcessTask(context, channel, mode, overrides, project, resource, debug);
            
            if(latch.start(data)) {
               Thread thread = factory.newThread(harness);
               ClassLoader loader = ClassPathUpdater.updateClassPath(dependencies);
               String[] array = arguments.toArray(empty);
               
               overrides.addAttribute(ProcessModel.SHORT_ARGUMENTS, array);
               overrides.addAttribute(ProcessModel.LONG_ARGUMENTS, array);
               
               if(loader == null) {
                  logger.info("Could not update dependencies");
               }
               checker.register(connector);
               connector.connect();
               thread.start();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
   
   public void attachProcess(ProcessEventChannel channel, String project, String resource) {
      ExecuteLatch latch = context.getLatch();
      String process = context.getProcess();
      
      try {         
         if(resource != null) {
            ExecuteData data = new ExecuteData(process, project, resource, null, true);
            ProgressReporter reporter = new ProgressReporter(context, channel, project, resource, true);
            ConsoleConnector connector = new ConsoleConnector(channel, process);
            
            if(latch.start(data)) {
               reporter.reportExecuting();
               checker.register(connector);
               connector.connect();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
}