package org.ternlang.studio.core.command;

public class BeginCommandMarshaller extends ObjectCommandMarshaller<BeginCommand>{
   
   public BeginCommandMarshaller() {
      super(CommandType.BEGIN);
   }
}