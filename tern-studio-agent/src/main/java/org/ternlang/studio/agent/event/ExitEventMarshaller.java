package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.EXIT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.ternlang.studio.agent.ProcessMode;

public class ExitEventMarshaller implements ProcessEventMarshaller<ExitEvent> {

   @Override
   public ExitEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String type = input.readUTF();
      ProcessMode mode = ProcessMode.resolveMode(type);
      long duration = input.readLong();
      
      return new ExitEvent.Builder(process)
         .withDuration(duration)
         .withMode(mode)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(ExitEvent value) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      ProcessMode mode = value.getMode();
      String process = value.getProcess();
      String type = mode.name();
      long duration = value.getDuration();
      
      output.writeUTF(process);
      output.writeUTF(type);
      output.writeLong(duration);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(EXIT.code, array, 0, array.length);
   }
}