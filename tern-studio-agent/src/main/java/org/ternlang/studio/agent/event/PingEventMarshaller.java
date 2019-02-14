package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.PING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PingEventMarshaller implements ProcessEventMarshaller<PingEvent> {

   @Override
   public PingEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      long time = input.readLong();
      
      return new PingEvent.Builder(process)
         .withTime(time)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(PingEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      long time = event.getTime();
      
      output.writeUTF(process);
      output.writeLong(time);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(PING.code, array, 0, array.length);
   }
}