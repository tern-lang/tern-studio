package org.ternlang.studio.agent.debug;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.trace.Trace;
import org.ternlang.core.trace.TraceListener;

public class TraceAdapter implements TraceListener {
   public void traceBefore(Scope scope, Trace trace) {}
   public void traceAfter(Scope scope, Trace trace) {}
   public void traceCompileError(Scope scope, Trace trace, Exception cause) {}
   public void traceRuntimeError(Scope scope, Trace trace, Exception cause) {}

}