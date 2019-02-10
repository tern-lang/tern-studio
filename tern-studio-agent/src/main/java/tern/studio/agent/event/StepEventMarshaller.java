package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.STEP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StepEventMarshaller implements ProcessEventMarshaller<StepEvent> {

   @Override
   public StepEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String thread = input.readUTF();
      int type = input.readInt();
      
      return new StepEvent.Builder(process)
         .withThread(thread)
         .withType(type)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(StepEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String thread = event.getThread();
      int type = event.getType();
      
      output.writeUTF(process);
      output.writeUTF(thread);
      output.writeInt(type);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(STEP.code, array, 0, array.length);
   }

}