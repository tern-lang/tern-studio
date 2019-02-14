package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.WRITE_ERROR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WriteErrorEventMarshaller implements ProcessEventMarshaller<WriteErrorEvent> {

   @Override
   public WriteErrorEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      int size = input.readInt();
      byte[] chunk = new byte[size];
      
      input.readFully(chunk, 0, size);
      
      return new WriteErrorEvent.Builder(process)
         .withData(chunk)
         .withOffset(0)
         .withLength(size)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(WriteErrorEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      byte[] chunk = event.getData();
      int length = event.getLength();
      int offset = event.getOffset();
      
      output.writeUTF(process);
      output.writeInt(length);
      output.write(chunk, offset, length);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(WRITE_ERROR.code, array, 0, array.length);
   }
}