package tern.studio.service.command;

public class ProblemCommandMarshaller extends ObjectCommandMarshaller<ProblemCommand>{
   
   public ProblemCommandMarshaller() {
      super(CommandType.PROBLEM);
   }
}