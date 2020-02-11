package org.ternlang.studio.agent.task;

import static org.ternlang.studio.agent.core.ExecuteStatus.COMPILING;
import static org.ternlang.studio.agent.core.ExecuteStatus.DEBUGGING;
import static org.ternlang.studio.agent.core.ExecuteStatus.FINISHED;
import static org.ternlang.studio.agent.core.ExecuteStatus.RUNNING;
import static org.ternlang.studio.agent.core.ExecuteStatus.TERMINATING;

import java.util.List;
import java.util.SortedSet;

import org.ternlang.agent.message.common.ExecuteData;
import org.ternlang.agent.message.common.ProfileResultArrayBuilder;
import org.ternlang.compile.verify.VerifyError;
import org.ternlang.core.trace.Trace;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.core.ExecuteState;
import org.ternlang.studio.agent.core.TerminateHandler;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.profiler.ProfileResult;
import org.ternlang.studio.agent.profiler.TraceProfiler;

public class ProgressReporter {
   
   private final ProcessEventChannel channel;
   private final ProcessContext context;
   private final String project;
   private final String resource;
   private final boolean debug;
   
   public ProgressReporter(ProcessContext context, ProcessEventChannel channel, String project, String resource, boolean debug) {
      this.context = context;   
      this.channel = channel;
      this.project = project;
      this.resource = resource;
      this.debug = debug;
   }  
   
   public void reportCompiling() {
      ExecuteLatch latch = context.getLatch();    
      ExecuteState state = latch.getState();
      ExecuteData data = state.getData();
      String process = state.getProcess();
      String system = state.getSystem();
      String project = data.project().toString();
      String pid = state.getPid();
      long duration = latch.update(COMPILING);
      
      if(duration >= 0) {
         try {
            channel.begin()
               .progress()
               .process(process)
               .pid(pid)
               .system(system)
               .project(project)
               .resource(resource)
               .status(org.ternlang.agent.message.common.ExecuteStatus.COMPILING)
               .totalTime(context.getTimeLimiter().getTimeLimit().getTimeout())
               .usedTime(0)
               .totalMemory(Runtime.getRuntime().totalMemory())
               .usedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .threads(Thread.getAllStackTraces().size()); // this might be expensive

            channel.send();
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportExecuting() {
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      ExecuteData data = state.getData();
      String process = state.getProcess();
      String system = state.getSystem();
      String project = data.project().toString();
      String pid = state.getPid();
      long duration = latch.update(debug ? DEBUGGING : RUNNING);
      
      if(duration >= 0) {
         long remainingTime = Math.max(0, context.getTimeLimiter().getTimeLimit().getExpiryTime() - System.currentTimeMillis());
         long timeout = context.getTimeLimiter().getTimeLimit().getTimeout();
         
         try {
            channel.begin()
               .begin()
               .process(process)
               .mode(org.ternlang.agent.message.common.ProcessMode.resolve(context.getMode().name()))
               .duration(duration)
               .pid(pid)
               .system(system)
               .project(project)
               .resource(resource)
               .status(debug ?
                       org.ternlang.agent.message.common.ExecuteStatus.DEBUGGING :
                       org.ternlang.agent.message.common.ExecuteStatus.RUNNING)
               .totalTime(timeout)
               .usedTime(timeout - remainingTime)
               .totalMemory(Runtime.getRuntime().totalMemory())
               .usedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .threads(Thread.getAllStackTraces().size()); // this might be expensive
            
            channel.send();
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }   
   
   public void reportError(List<VerifyError> errors) {
      String process = context.getProcess();   
      
      for(VerifyError error : errors) {
         Throwable cause = error.getCause();
         String description = ExceptionBuilder.buildRoot(cause);
         Trace trace = error.getTrace();
         String message = cause.getMessage();
         String path = trace.getPath().toString();
         int line = trace.getLine();
         
         try {
            channel.begin()
               .scriptError()
               .process(process)
               .description(description)
               .message(message)
               .resource(path)
               .line(line);

            channel.send();
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportTerminating() {   
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      String process = state.getProcess();
      String system = state.getSystem();
      String pid = state.getPid();
      long duration = latch.update(TERMINATING);
      
      if(duration >= 0) {
         long timeout = context.getTimeLimiter().getTimeLimit().getTimeout();
         
         try {
            channel.begin()
               .progress()
               .process(process)
               .pid(pid)
               .system(system)
               .project(project)
               .resource(resource)
               .status(org.ternlang.agent.message.common.ExecuteStatus.TERMINATING)
               .usedTime(timeout)
               .totalTime(timeout)
               .totalMemory(Runtime.getRuntime().totalMemory())
               .usedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .threads(Thread.getAllStackTraces().size()); // this might be expensive

            channel.send();
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportProfile() {   
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      TraceProfiler profiler = context.getProfiler();
      SortedSet<ProfileResult> results = profiler.lines(200);
      String process = state.getProcess();
      
      try {
         ProfileResultArrayBuilder builder = channel.begin()
            .profile()
            .process(process)
            .results();

         for(ProfileResult result : results) {
            builder.add()
               .time(result.getTime())
               .count(result.getCount())
               .resource(result.getResource())
               .line(result.getLine());
         }
         channel.send();
      } catch(Exception e) {
         ConsoleFlusher.flushError(e);
      }
   }
   
   public void reportFinished(long totalTime) {
      ProcessMode mode = context.getMode();
      ExecuteLatch latch = context.getLatch();  
      String process = context.getProcess();  
      long duration = latch.update(FINISHED);
      
      if(duration >= 0) {
         try {
            channel.begin()
               .exit()
               .process(process)
               .duration(totalTime)
               .mode(org.ternlang.agent.message.common.ProcessMode.resolve(context.getMode().name()));

            channel.send();
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
      if(mode.isTerminateRequired()) {
         TerminateHandler.terminate("Task has finished executing"); // shutdown when finished
      }
   }
}
