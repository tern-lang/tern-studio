package org.ternlang.studio.agent.task;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.ternlang.agent.message.common.ProgramArgumentArray;
import org.ternlang.common.thread.ThreadBuilder;
import org.ternlang.core.scope.Model;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.ProcessModel;
import org.ternlang.studio.agent.client.ConnectionChecker;
import org.ternlang.studio.agent.core.ClassPathUpdater;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.log.TraceLogger;

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

   public void beginExecute(
       ProcessEventChannel channel,
       String project,
       String resource,
       String dependencies,
       ProgramArgumentArray arguments,
       boolean debug)
   {
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