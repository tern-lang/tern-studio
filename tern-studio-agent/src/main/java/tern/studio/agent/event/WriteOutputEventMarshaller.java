package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.WRITE_OUTPUT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WriteOutputEventMarshaller implements ProcessEventMarshaller<WriteOutputEvent> {

   @Override
   public WriteOutputEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      int size = input.readInt();
      byte[] chunk = new byte[size];
      
      input.readFully(chunk, 0, size);
      
      return new WriteOutputEvent.Builder(process)
         .withData(chunk)
         .withOffset(0)
         .withLength(size)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(WriteOutputEvent event) throws IOException {
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
      return new MessageEnvelope(WRITE_OUTPUT.code, array, 0, array.length);
   }
}