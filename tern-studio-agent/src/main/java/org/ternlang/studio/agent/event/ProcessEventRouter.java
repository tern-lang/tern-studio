package org.ternlang.studio.agent.event;


public class ProcessEventRouter {

   private final ProcessEventListener listener;
   
   public ProcessEventRouter(ProcessEventListener listener) {
      this.listener = listener;
   }
   
   public void route(ProcessEventChannel channel, ProcessEvent event) throws Exception {
      if(event instanceof ExitEvent) {
         listener.onExit(channel, (ExitEvent)event);
      } else if(event instanceof ExecuteEvent) {
         listener.onExecute(channel, (ExecuteEvent)event);                  
      } else if(event instanceof RegisterEvent) {
         listener.onRegister(channel, (RegisterEvent)event);
      } else if(event instanceof ScriptErrorEvent) {
         listener.onScriptError(channel, (ScriptErrorEvent)event);
      } else if(event instanceof WriteErrorEvent) {
         listener.onWriteError(channel, (WriteErrorEvent)event);
      } else if(event instanceof WriteOutputEvent) {
         listener.onWriteOutput(channel, (WriteOutputEvent)event);
      } else if(event instanceof PingEvent) {
         listener.onPing(channel, (PingEvent)event);
      } else if(event instanceof PongEvent) {
         listener.onPong(channel, (PongEvent)event);
      } else if(event instanceof ScopeEvent) {
         listener.onScope(channel, (ScopeEvent)event);
      } else if(event instanceof BreakpointsEvent) {
         listener.onBreakpoints(channel, (BreakpointsEvent)event);
      } else if(event instanceof BeginEvent) {
         listener.onBegin(channel, (BeginEvent)event);
      } else if(event instanceof StepEvent) {
         listener.onStep(channel, (StepEvent)event);
      } else if(event instanceof BrowseEvent) {
         listener.onBrowse(channel, (BrowseEvent)event);
      } else if(event instanceof EvaluateEvent) {
         listener.onEvaluate(channel, (EvaluateEvent)event);                  
      } else if(event instanceof ProfileEvent) {
         listener.onProfile(channel, (ProfileEvent)event);
      } else if(event instanceof EvaluateEvent) {
         listener.onEvaluate(channel, (EvaluateEvent)event);
      } else if(event instanceof FaultEvent) {
         listener.onFault(channel, (FaultEvent)event);
      }
   }
}