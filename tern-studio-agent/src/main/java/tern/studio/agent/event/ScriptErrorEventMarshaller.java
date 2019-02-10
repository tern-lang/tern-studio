package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.SCRIPT_ERROR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ScriptErrorEventMarshaller implements ProcessEventMarshaller<ScriptErrorEvent> {

   @Override
   public ScriptErrorEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String resource = input.readUTF();
      String description = input.readUTF();
      int line = input.readInt();
      
      return new ScriptErrorEvent.Builder(process)
         .withResource(resource)
         .withDescription(description)
         .withLine(line)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(ScriptErrorEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      String resource = event.getResource();
      String description = event.getDescription();
      int line = event.getLine();
      
      output.writeUTF(process);
      output.writeUTF(resource);
      output.writeUTF(description);
      output.writeInt(line);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(SCRIPT_ERROR.code, array, 0, array.length);
   }
}