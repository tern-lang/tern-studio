package org.ternlang.studio.agent.debug;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ternlang.core.Context;
import org.ternlang.core.function.Function;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.stack.ThreadStack;
import org.ternlang.core.trace.Trace;
import org.ternlang.core.trace.TraceType;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ScopeEvent;

public class SuspendInterceptor extends TraceAdapter {
   
   private static final String THREAD_NAME = "%s: %s@%s";

   private final ProcessEventChannel channel;
   private final ThreadProgressLocal monitor;
   private final AtomicInteger counter;
   private final SuspendController latch;
   private final ProcessMode mode;
   private final String process;
   
   public SuspendInterceptor(ProcessEventChannel channel, BreakpointMatcher matcher, SuspendController latch, ProcessMode mode, String process) {
      this.monitor = new ThreadProgressLocal(matcher);
      this.counter = new AtomicInteger();
      this.channel = channel;
      this.process = process;
      this.latch = latch;
      this.mode = mode;
   }

   @Override
   public void traceBefore(Scope scope, Trace trace) {
      ThreadProgress progress = monitor.get();
      TraceType type = trace.getType();
      Module module = scope.getModule();
      Path source = trace.getPath();
      String resource = source.getPath();
      int line = trace.getLine();
      
      if(progress.isSuspendBefore(trace)) { 
         try {
            String thread = Thread.currentThread().getName();
            int count = counter.getAndIncrement();
            int depth = progress.currentDepth();
            Context context = module.getContext();
            ThreadStack stack = context.getStack();
            Function function = stack.current(); // we can determine the function type
            String path = ResourceExtractor.extractResource(resource);
            ThreadStackGenerator generator = new ThreadStackGenerator(stack);
            String threads = generator.generate();
            String threadName = String.format(THREAD_NAME, ScopeNotifier.class.getSimpleName(), path, line);
            ScopeExtractor extractor = new ScopeExtractor(context, scope, function, path);
            ScopeEventBuilder builder = new ScopeEventBuilder(extractor, type, process, thread, threads, path, line, depth, count);
            ScopeNotifier notifier = new ScopeNotifier(builder, mode, threadName);
            ScopeEvent suspend = builder.suspendEvent(mode);
            ScopeEvent resume = builder.resumeEvent(mode);
            
            progress.clear(); // clear config
            channel.send(suspend);
            notifier.start();
            suspend(notifier, extractor, resource, line);
            channel.send(resume);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      progress.beforeInstruction(type);
   }

   @Override
   public void traceAfter(Scope scope, Trace trace) {
      ThreadProgress progress = monitor.get();
      TraceType type = trace.getType();
      Module module = scope.getModule();
      Path source = trace.getPath();
      String resource = source.getPath();
      int line = trace.getLine();
      
      if(progress.isSuspendAfter(trace)) { 
         try {
            String thread = Thread.currentThread().getName();
            int count = counter.getAndIncrement();
            int depth = progress.currentDepth();
            Context context = module.getContext();
            ThreadStack stack = context.getStack();
            Function function = stack.current(); // we can determine the function type
            String path = ResourceExtractor.extractResource(resource);
            ThreadStackGenerator generator = new ThreadStackGenerator(stack);
            String threads = generator.generate();
            String threadName = String.format(THREAD_NAME, ScopeNotifier.class.getSimpleName(), path, line);
            ScopeExtractor extractor = new ScopeExtractor(context, scope, function, path);
            ScopeEventBuilder builder = new ScopeEventBuilder(extractor, type, process, thread, threads, path, line, depth, count);
            ScopeNotifier notifier = new ScopeNotifier(builder, mode, threadName);
            ScopeEvent suspend = builder.suspendEvent(mode);
            ScopeEvent resume = builder.resumeEvent(mode);
            
            progress.clear(); // clear config
            channel.send(suspend);
            notifier.start();
            suspend(notifier, extractor, resource, line);
            channel.send(resume);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      progress.afterInstruction(type);
   }
   
   private void suspend(ScopeNotifier notifier, ScopeBrowser browser, String resource, int line) {
      ResumeType type = latch.suspend(notifier, browser);
      ThreadProgress step = monitor.get();
      
      step.resume(type);
   }
   
   private class ScopeNotifier extends Thread implements ResumeListener {
      
      private final ScopeEventBuilder builder;
      private final AtomicBoolean active;
      private final ProcessMode mode;
      
      public ScopeNotifier(ScopeEventBuilder builder, ProcessMode mode, String name) {
         this.active = new AtomicBoolean(true);
         this.builder = builder;
         this.setName(name);
         this.mode = mode;
      }

      @Override
      public void run() {
         try {
            while(active.get()) {
               Thread.sleep(400);
               
               if(active.get()) {
                  ScopeEvent event = builder.suspendEvent(mode);
                  channel.send(event);
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            active.set(false);
         }
      }

      @Override
      public void resume(String thread) {
         active.set(false);
      }
   }

}