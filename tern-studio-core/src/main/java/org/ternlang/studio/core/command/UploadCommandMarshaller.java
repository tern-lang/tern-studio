package org.ternlang.studio.core.command;

public class UploadCommandMarshaller extends ObjectCommandMarshaller<UploadCommand>{
   
   public UploadCommandMarshaller() {
      super(CommandType.UPLOAD);
   }
}