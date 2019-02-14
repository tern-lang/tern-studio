package org.ternlang.studio.agent.event;

public enum ProcessEventType {
   WRITE_OUTPUT(WriteOutputEventMarshaller.class, WriteOutputEvent.class, 1),
   WRITE_ERROR(WriteErrorEventMarshaller.class, WriteErrorEvent.class, 2),
   PING(PingEventMarshaller.class, PingEvent.class, 3),
   PONG(PongEventMarshaller.class, PongEvent.class, 4),
   EXECUTE(ExecuteEventMarshaller.class, ExecuteEvent.class, 5),
   REGISTER(RegisterEventMarshaller.class, RegisterEvent.class, 6),
   SCRIPT_ERROR(ScriptErrorEventMarshaller.class, ScriptErrorEvent.class, 7),
   EXIT(ExitEventMarshaller.class, ExitEvent.class, 8),
   SCOPE(ScopeEventMarshaller.class, ScopeEvent.class, 9),
   BREAKPOINTS(BreakpointsEventMarshaller.class, BreakpointsEvent.class, 10),
   STEP(StepEventMarshaller.class, StepEvent.class, 11),
   START(BeginEventMarshaller.class, BeginEvent.class, 12),
   BROWSE(BrowseEventMarshaller.class, BrowseEvent.class, 13),
   PROFILE(ProfileEventMarshaller.class, ProfileEvent.class, 14),
   EVALUATE(EvaluateEventMarshaller.class, EvaluateEvent.class, 15),
   FAULT(FaultEventMarshaller.class, FaultEvent.class, 16),
   PROGRESS(ProgressEventMarshaller.class, ProgressEvent.class, 17);
   
   public final Class<? extends ProcessEventMarshaller> marshaller;
   public final Class<? extends ProcessEvent> event;
   public final int code;
   
   private ProcessEventType(Class<? extends ProcessEventMarshaller> marshaller, Class<? extends ProcessEvent> event, int code) {
      this.marshaller = marshaller;
      this.event = event;
      this.code = code;
   }
}