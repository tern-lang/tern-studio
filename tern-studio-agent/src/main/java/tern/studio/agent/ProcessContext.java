package tern.studio.agent;

import java.util.concurrent.Executor;

import tern.common.thread.ThreadPool;
import tern.compile.ResourceCompiler;
import tern.compile.StoreContext;
import tern.core.Context;
import tern.core.ExpressionEvaluator;
import tern.core.ResourceManager;
import tern.core.link.PackageLinker;
import tern.core.scope.EmptyModel;
import tern.core.scope.Model;
import tern.core.trace.TraceInterceptor;
import tern.studio.agent.core.ExecuteLatch;
import tern.studio.agent.debug.BreakpointMatcher;
import tern.studio.agent.debug.SuspendController;
import tern.studio.agent.profiler.TraceProfiler;

public class ProcessContext {

   private final SuspendController controller;
   private final ResourceCompiler compiler;
   private final TraceProfiler profiler;
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