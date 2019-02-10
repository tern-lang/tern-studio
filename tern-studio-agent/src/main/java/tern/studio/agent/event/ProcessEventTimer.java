package tern.studio.agent.event;

import tern.studio.agent.log.TraceLogger;

public class ProcessEventTimer implements ProcessEventListener {
   
   private final ProcessEventListener listener;
   private final TraceLogger logger;
   
   public ProcessEventTimer(ProcessEventListener listener, TraceLogger logger) {
      this.listener = listener;
      this.logger = logger;
   }

   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onExit(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onExit(): took " + duration + " ms");
      }
   }

   @Override
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onExecute(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onExecute(): took " + duration + " ms");
      }
   }

   @Override
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onWriteError(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onWriteError(): took " + duration + " ms");
      }
   }

   @Override
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onWriteOutput(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onWriteOutput(): took " + duration + " ms");
      }
   }

   @Override
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onRegister(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onRegister(): took " + duration + " ms");
      }
   }

   @Override
   public void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onScriptError(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onSyntaxError(): took " + duration + " ms");
      }
   }

   @Override
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onScope(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onScope(): took " + duration + " ms");
      }
   }

   @Override
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBreakpoints(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onBreakpoints(): took " + duration + " ms");
      }
   }

   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBegin(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onBegin(): took " + duration + " ms");
      }
   }

   @Override
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onStep(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onStep(): took " + duration + " ms");
      }
   }

   @Override
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBrowse(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onBrowse(): took " + duration + " ms");
      }
   }

   @Override
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onProfile(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onProfile(): took " + duration + " ms");
      }
   }
   

   @Override
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onEvaluate(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onEvaluate(): took " + duration + " ms");
      }
   }
   
   @Override
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onFault(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onFault(): took " + duration + " ms");
      }
   }

   @Override
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onPing(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onPing(): took " + duration + " ms");
      }
   }

   @Override
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onPong(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onPong(): took " + duration + " ms");
      }
   }

   @Override
   public void onClose(ProcessEventChannel channel) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onClose(channel);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.trace("onClose(): took " + duration + " ms");
      }
   }
}