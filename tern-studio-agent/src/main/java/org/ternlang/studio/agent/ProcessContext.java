package org.ternlang.studio.agent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.ternlang.common.thread.ThreadPool;
import org.ternlang.compile.ResourceCompiler;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.core.ExpressionEvaluator;
import org.ternlang.core.ResourceManager;
import org.ternlang.core.link.PackageLinker;
import org.ternlang.core.scope.EmptyModel;
import org.ternlang.core.scope.Model;
import org.ternlang.core.trace.TraceInterceptor;
import org.ternlang.studio.agent.core.ExecuteLatch;
import org.ternlang.studio.agent.debug.BreakpointMatcher;
import org.ternlang.studio.agent.debug.SuspendController;
import org.ternlang.studio.agent.limit.TimeLimiter;
import org.ternlang.studio.agent.profiler.TraceProfiler;

public class ProcessContext {

   private final SuspendController controller;
   private final ResourceCompiler compiler;
   private final TraceProfiler profiler;
   private final TimeLimiter limiter;
   private final BreakpointMatcher matcher;
   private final ExecuteLatch latch;
   private final ProcessStore store;
   private final ProcessMode mode;
   private final Executor executor;
   private final Context context;
   private final Model model;   
   private final String process;

   public ProcessContext(ProcessMode mode, ProcessStore store, String process) {
      this(mode, store, process, 10);
   }
   
   public ProcessContext(ProcessMode mode, ProcessStore store, String process, int threads) {
      this(mode, store, process, threads, 0);
   }
   
   public ProcessContext(ProcessMode mode, ProcessStore store, String process, int threads, int stack) {
      this.executor = new ThreadPool(threads < 5 ? 5 : threads, 100, stack);
      this.latch = new ExecuteLatch(process);
      this.context = new StoreContext(store, executor);
      this.compiler = new ResourceCompiler(context);
      this.controller = new SuspendController();
      this.matcher = new BreakpointMatcher();
      this.profiler = new TraceProfiler();
      this.limiter = new TimeLimiter();
      this.model = new EmptyModel();
      this.process = process;
      this.store = store;
      this.mode = mode;
   }
   
   public ProcessMode getMode() {
      return mode;
   }

   public ExecuteLatch getLatch() {
      return latch;
   }

   public ResourceManager getManager(){
      return context.getManager();
   }
   
   public PackageLinker getLinker() {
      return context.getLinker();
   }
   
   public TimeLimiter getTimeLimiter() {
      return limiter;
   }
   
   public TraceInterceptor getInterceptor() {
      return context.getInterceptor();
   }
   
   public ResourceCompiler getCompiler() {
      return compiler;
   }
   
   public ExpressionEvaluator getEvaluator(){
      return context.getEvaluator();
   }
   
   public TraceProfiler getProfiler() {
      return profiler;
   }
   
   public BreakpointMatcher getMatcher() {
      return matcher;
   }
   
   public SuspendController getController() {
      return controller;
   }
   
   public ProcessStore getStore() {
      return store;
   }
   
   public Model getModel() {
      return model;
   }
   
   public String getProcess() {
      return process;
   }
}