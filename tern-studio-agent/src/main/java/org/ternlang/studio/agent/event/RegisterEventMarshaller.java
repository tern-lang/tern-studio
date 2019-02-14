package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.REGISTER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterEventMarshaller implements ProcessEventMarshaller<RegisterEvent> {

   @Override
   public RegisterEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String system = input.readUTF();
      String pid = input.readUTF();

      return new RegisterEvent.Builder(process)
         .withPid(pid)
         .withSystem(system)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(RegisterEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String system = event.getSystem();
      String pid = event.getPid();

      output.writeUTF(process);
      output.writeUTF(system);
      output.writeUTF(pid);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(REGISTER.code, array, 0, array.length);
   }
}