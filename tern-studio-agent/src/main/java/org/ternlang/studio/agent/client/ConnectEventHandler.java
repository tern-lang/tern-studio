package org.ternlang.studio.agent.client;

import org.ternlang.agent.message.event.BeginEvent;
import org.ternlang.agent.message.event.BreakpointsEvent;
import org.ternlang.agent.message.event.BrowseEvent;
import org.ternlang.agent.message.event.EvaluateEvent;
import org.ternlang.agent.message.event.ExecuteEvent;
import org.ternlang.agent.message.event.ExitEvent;
import org.ternlang.agent.message.event.FaultEvent;
import org.ternlang.agent.message.event.PingEvent;
import org.ternlang.agent.message.event.PongEvent;
import org.ternlang.agent.message.event.ProcessEventHandler;
import org.ternlang.agent.message.event.ProfileEvent;
import org.ternlang.agent.message.event.ProgressEvent;
import org.ternlang.agent.message.event.RegisterEvent;
import org.ternlang.agent.message.event.ScopeEvent;
import org.ternlang.agent.message.event.ScriptErrorEvent;
import org.ternlang.agent.message.event.StepEvent;
import org.ternlang.agent.message.event.WriteErrorEvent;
import org.ternlang.agent.message.event.WriteOutputEvent;
import org.ternlang.studio.agent.event.ProcessEventChannel;
import org.ternlang.studio.agent.event.ProcessEventListener;

public class ConnectEventHandler implements ProcessEventHandler {

    private final ProcessEventListener listener;
    private final ProcessEventChannel channel;

    public ConnectEventHandler(ProcessEventChannel channel, ProcessEventListener listener) {
        this.listener = listener;
        this.channel = channel;
    }

    @Override
    public void onBegin(BeginEvent begin) {
        try {
            listener.onBegin(channel, begin);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onBreakpoints(BreakpointsEvent breakpoints) {
        try {
            listener.onBreakpoints(channel, breakpoints);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onBrowse(BrowseEvent browse) {
        try {
            listener.onBrowse(channel, browse);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onEvaluate(EvaluateEvent evaluate) {
        try {
            listener.onEvaluate(channel, evaluate);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onExecute(ExecuteEvent execute) {
        try {
            listener.onExecute(channel, execute);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onExit(ExitEvent exit) {
        try {
            listener.onExit(channel, exit);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onFault(FaultEvent fault) {
        try {
            listener.onFault(channel, fault);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onPing(PingEvent ping) {
        try {
            listener.onPing(channel, ping);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onPong(PongEvent pong) {
        try {
            listener.onPong(channel, pong);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onProgress(ProgressEvent progress) {
        throw new IllegalStateException("Could not process event");
    }

    @Override
    public void onProfile(ProfileEvent profile) {
        try {
            listener.onProfile(channel, profile);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onRegister(RegisterEvent register) {
        try {
            listener.onRegister(channel, register);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onScope(ScopeEvent scope) {
        try {
            listener.onScope(channel, scope);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onScriptError(ScriptErrorEvent scriptError) {
        try {
            listener.onScriptError(channel, scriptError);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onStep(StepEvent step) {
        try {
            listener.onStep(channel, step);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onWriteError(WriteErrorEvent writeError) {
        try {
            listener.onWriteError(channel, writeError);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }

    @Override
    public void onWriteOutput(WriteOutputEvent writeOutput) {
        try {
            listener.onWriteOutput(channel, writeOutput);
        } catch(Exception e) {
            throw new IllegalStateException("Could not process event", e);
        }
    }
}
