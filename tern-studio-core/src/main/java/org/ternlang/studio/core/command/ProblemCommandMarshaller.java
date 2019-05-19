package org.ternlang.studio.core.command;

public class ProblemCommandMarshaller extends ObjectCommandMarshaller<ProblemCommand>{
   
   public ProblemCommandMarshaller() {
      super(CommandType.PROBLEM);
   }
}