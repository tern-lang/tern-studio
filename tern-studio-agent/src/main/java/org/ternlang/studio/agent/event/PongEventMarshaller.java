package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.PONG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.ternlang.studio.agent.core.ExecuteStatus;

public class PongEventMarshaller<T extends PongEvent> implements ProcessEventMarshaller<T> {
   
   private final int code;
   
   public PongEventMarshaller() {
      this(PONG.code);
   }
   
   public PongEventMarshaller(int code) {
      this.code = code;
   }   

   @Override
   public T fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String system = input.readUTF();
      String pid = input.readUTF();
      String status = input.readUTF();
      
      if(input.readBoolean()) {
         String project = input.readUTF();
         String resource = input.readUTF();
         long totalMemory = input.readLong();
         long usedMemory = input.readLong();
         int threads = input.readInt();
         
         return (T)getBuilder(process)
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
      return (T)getBuilder(process)
         .withPid(pid)
         .withSystem(system)
         .withStatus(ExecuteStatus.resolveStatus(status))
         .build();
   }   

   @Override
   public MessageEnvelope toMessage(T event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      String process = event.getProcess();
      ExecuteStatus status = event.getStatus();
      String resource = event.getResource();
      String project = event.getProject();
      String system = event.getSystem();
      String pid = event.getPid();
      long totalMemory = event.getTotalMemory();
      long usedMemory = event.getUsedMemory();
      int threads = event.getThreads();
      
      output.writeUTF(process);
      output.writeUTF(system);
      output.writeUTF(pid);
      output.writeUTF(status.name());
      
      if(status.isStarted()) {
         output.writeBoolean(true);
         output.writeUTF(project);
         output.writeUTF(resource);
         output.writeLong(totalMemory);
         output.writeLong(usedMemory);
         output.writeInt(threads);
      } else {
         output.writeBoolean(false);
      }
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(code, array, 0, array.length);
   }
   
   protected PongEvent.Builder getBuilder(String process) {
      return new PongEvent.Builder(process);
   }
}