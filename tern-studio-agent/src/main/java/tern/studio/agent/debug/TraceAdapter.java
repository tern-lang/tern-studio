package tern.studio.agent.debug;

import tern.core.scope.Scope;
import tern.core.trace.Trace;
import tern.core.trace.TraceListener;

public class TraceAdapter implements TraceListener {
   public void traceBefore(Scope scope, Trace trace) {}
   public void traceAfter(Scope scope, Trace trace) {}
   public void traceCompileError(Scope scope, Trace trace, Exception cause) {}
   public void traceRuntimeError(Scope scope, Trace trace, Exception cause) {}

}