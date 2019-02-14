package org.ternlang.studio.service.command;

public class BeginCommandMarshaller extends ObjectCommandMarshaller<BeginCommand>{
   
   public BeginCommandMarshaller() {
      super(CommandType.BEGIN);
   }
}