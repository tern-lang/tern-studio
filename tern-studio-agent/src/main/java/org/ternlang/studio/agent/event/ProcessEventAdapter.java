package org.ternlang.studio.agent.event;

import org.ternlang.agent.message.event.BeginEvent;
import org.ternlang.agent.message.event.BreakpointsEvent;
import org.ternlang.agent.message.event.BrowseEvent;
import org.ternlang.agent.message.event.EvaluateEvent;
import org.ternlang.agent.message.event.ExecuteEvent;
import org.ternlang.agent.message.event.ExitEvent;
import org.ternlang.agent.message.event.FaultEvent;
import org.ternlang.agent.message.event.PingEvent;
import org.ternlang.agent.message.event.PongEvent;
import org.ternlang.agent.message.event.ProfileEvent;
import org.ternlang.agent.message.event.RegisterEvent;
import org.ternlang.agent.message.event.ScopeEvent;
import org.ternlang.agent.message.event.ScriptErrorEvent;
import org.ternlang.agent.message.event.StepEvent;
import org.ternlang.agent.message.event.WriteErrorEvent;
import org.ternlang.agent.message.event.WriteOutputEvent;

public class ProcessEventAdapter implements ProcessEventListener {
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {}
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {}
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {}
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {}
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {}
   public void onScriptError(ProcessEventChannel channel, ScriptErrorEvent event) throws Exception {}
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {}
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {}
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {}
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {}
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {}
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {}
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {}
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {}
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {}
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {}
   public void onClose(ProcessEventChannel channel) throws Exception {}

}