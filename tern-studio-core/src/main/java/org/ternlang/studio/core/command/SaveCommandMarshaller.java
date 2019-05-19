package org.ternlang.studio.core.command;

public class SaveCommandMarshaller extends ObjectCommandMarshaller<SaveCommand>{
   
   public SaveCommandMarshaller() {
      super(CommandType.SAVE);
   }
}