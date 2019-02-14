package org.ternlang.studio.service.command;

public class UploadCommandMarshaller extends ObjectCommandMarshaller<UploadCommand>{
   
   public UploadCommandMarshaller() {
      super(CommandType.UPLOAD);
   }
}