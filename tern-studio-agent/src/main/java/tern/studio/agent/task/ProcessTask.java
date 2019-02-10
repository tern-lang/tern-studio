package tern.studio.agent.task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import tern.compile.Executable;
import tern.compile.ResourceCompiler;
import tern.compile.verify.VerifyError;
import tern.compile.verify.VerifyException;
import tern.core.scope.Model;
import tern.studio.agent.ProcessContext;
import tern.studio.agent.ProcessMode;
import tern.studio.agent.event.ProcessEventChannel;
import tern.studio.agent.profiler.TraceProfiler;
import tern.studio.agent.profiler.ProfileResultUpdater;

public class ProcessTask implements Runnable {
   
   private final ProgressReporter reporter;
   private final ProcessEventChannel client;
   private final ProcessContext context;
   private final ProcessMode mode;
   private final String resource;
   private final Model model;
   
   public ProcessTask(ProcessContext context, ProcessEventChannel client, ProcessMode mode, Model model, String project, String resource, boolean debug) {
      this.reporter = new ProgressReporter(context, client, project, resource, debug);
      this.client = client;
      this.resource = resource;
      this.context = context;
      this.model = model;
      this.mode = mode;
   }
   
   @Override
   public void run() {
      TraceProfiler profiler = context.getProfiler();
      ResourceCompiler compiler = context.getCompiler();
      String process = context.getProcess();
      long start = System.nanoTime();
      
      try {         
         ProfileResultUpdater updater = new ProfileResultUpdater(profiler, client);
         
         reporter.reportCompiling();         
         Executable executable = compiler.compile(resource);
         reporter.reportExecuting();
         
         try {
            updater.start(process); // start sending profile events!!
            executable.execute(model); // execute the script
         } catch(VerifyException e) {
            List<VerifyError> errors = e.getErrors();
            reporter.reportError(errors);
         } catch(Throwable cause) {
            ConsoleFlusher.flushError(cause);
         }finally {            
            try {
               if(mode.isTerminateRequired()) {
                  reporter.reportTerminating();
                  reporter.reportProfile(); // one last update
               }
               ConsoleFlusher.flush();               
            } catch(Exception cause) {
               ConsoleFlusher.flushError(cause);
            } 
         }
      } catch (Exception cause) {
         ConsoleFlusher.flushError(cause);
      } finally {
         if(mode.isTerminateRequired()) {
            long finish = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(finish - start);
         
            reporter.reportFinished(duration);
         }
      }
   }
}