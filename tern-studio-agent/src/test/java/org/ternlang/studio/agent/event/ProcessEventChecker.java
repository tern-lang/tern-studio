package org.ternlang.studio.agent.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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

public class ProcessEventChecker implements ProcessEventHandler {

   private final Map<Class, Consumer> consumers = new HashMap<Class, Consumer>();

   public boolean isDone() {
       return consumers.isEmpty();
   }

   public <T> void register(Class<T> type, Consumer<T> consumer) {
       consumers.put(type, consumer);
   }

   @Override
   public void onBegin(final BeginEvent begin) {
       Consumer consumer = consumers.remove(BeginEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(begin);
   }

   @Override
   public void onBreakpoints(final BreakpointsEvent breakpoints) {
       Consumer consumer = consumers.remove(BreakpointsEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(breakpoints);
   }

   @Override
   public void onBrowse(final BrowseEvent browse) {
       Consumer consumer = consumers.remove(BrowseEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(browse);
   }

   @Override
   public void onEvaluate(final EvaluateEvent evaluate) {
       Consumer consumer = consumers.remove(EvaluateEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(evaluate);
   }

   @Override
   public void onExecute(final ExecuteEvent execute) {
       Consumer consumer = consumers.remove(ExecuteEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(execute);
   }

   @Override
   public void onExit(ExitEvent event) {
       Consumer consumer = consumers.remove(ExitEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(event);
   }

   @Override
   public void onFault(FaultEvent fault) {
       Consumer consumer = consumers.remove(FaultEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(fault);
   }

   @Override
   public void onPing(PingEvent ping) {
       Consumer consumer = consumers.remove(PingEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(ping);
   }

   @Override
   public void onPong(PongEvent pong) {
       Consumer consumer = consumers.remove(PongEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(pong);
   }

   @Override
   public void onProgress(ProgressEvent progress) {
       Consumer consumer = consumers.remove(ProgressEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(progress);
   }

   @Override
   public void onProfile(ProfileEvent profile) {
       Consumer consumer = consumers.remove(ProfileEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(profile);
   }

   @Override
   public void onRegister(RegisterEvent register) {
       Consumer consumer = consumers.remove(RegisterEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(register);
   }

   @Override
   public void onScope(ScopeEvent scope) {
       Consumer consumer = consumers.remove(ScopeEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(scope);
   }

   @Override
   public void onScriptError(ScriptErrorEvent scriptError) {
       Consumer consumer = consumers.remove(ScriptErrorEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(scriptError);
   }

   @Override
   public void onStep(StepEvent step) {
       Consumer consumer = consumers.remove(StepEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(step);
   }

   @Override
   public void onWriteError(WriteErrorEvent writeError) {
       Consumer consumer = consumers.remove(WriteErrorEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(writeError);
   }

   @Override
   public void onWriteOutput(WriteOutputEvent writeOutput) {
       Consumer consumer = consumers.remove(WriteOutputEvent.class);

       if(consumer == null) {
           throw new IllegalStateException("Could not process event");
       }
       consumer.accept(writeOutput);
   }
}
