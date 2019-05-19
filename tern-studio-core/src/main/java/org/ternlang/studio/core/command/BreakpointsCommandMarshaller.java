package org.ternlang.studio.core.command;

public class BreakpointsCommandMarshaller extends ObjectCommandMarshaller<BreakpointsCommand>{
   
   public BreakpointsCommandMarshaller() {
      super(CommandType.BREAKPOINTS);
   }
}