package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.START;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import tern.studio.agent.ProcessMode;
import tern.studio.agent.core.ExecuteStatus;

public class BeginEventMarshaller implements ProcessEventMarshaller<BeginEvent> {

   @Override
   public BeginEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String system = input.readUTF();
      String pid = input.readUTF();
      String project = input.readUTF(); 
      String resource = input.readUTF();      
      String status = input.readUTF();
      String mode = input.readUTF();

      long totalMemory = input.readLong();
      long usedMemory = input.readLong();
      int threads = input.readInt();
      long duration = input.readLong();
      
      return new BeginEvent.Builder(process)
         .withMode(ProcessMode.resolveMode(mode))
         .withDuration(duration)
         .withPid(pid)
         .withSystem(system)
         .withProject(project)
         .withResource(resource)
         .withStatus(ExecuteStatus.resolveStatus(status))
         .withTotalMemory(totalMemory)
         .withUsedMemory(usedMemory)
         .withThreads(threads)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(BeginEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);      
      String process = event.getProcess();
      String system = event.getSystem();
      String pid = event.getPid();
      String project = event.getProject();
      String resource = event.getResource();
      ExecuteStatus status = event.getStatus();
      ProcessMode mode = event.getMode();
      long totalMemory = event.getTotalMemory();
      long usedMemory = event.getUsedMemory();
      int threads = event.getThreads();
      long duration = event.getDuration();
      
      output.writeUTF(process);
      output.writeUTF(system);
      output.writeUTF(pid);
      output.writeUTF(project);
      output.writeUTF(resource);
      output.writeUTF(status.name());
      output.writeUTF(mode.name());
      output.writeLong(totalMemory);
      output.writeLong(usedMemory);
      output.writeInt(threads);
      output.writeLong(duration);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(START.code, array, 0, array.length);
   }
}