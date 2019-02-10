package tern.studio.agent.event;

public interface ProcessEventListener {
   void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception;
   void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception;
   void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception;
   void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception;
   void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception;
   void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception;
   void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception;
   void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception;
   void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception;
   void onStep(ProcessEventChannel channel, StepEvent event) throws Exception;
   void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception;
   void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception;
   void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception;
   void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception;
   void onPing(ProcessEventChannel channel, PingEvent event) throws Exception;
   void onPong(ProcessEventChannel channel, PongEvent event) throws Exception;
   void onClose(ProcessEventChannel channel) throws Exception;
}