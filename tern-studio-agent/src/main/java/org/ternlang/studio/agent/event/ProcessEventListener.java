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