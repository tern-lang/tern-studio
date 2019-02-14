package org.ternlang.studio.service.command;

public class BreakpointsCommandMarshaller extends ObjectCommandMarshaller<BreakpointsCommand>{
   
   public BreakpointsCommandMarshaller() {
      super(CommandType.BREAKPOINTS);
   }
}