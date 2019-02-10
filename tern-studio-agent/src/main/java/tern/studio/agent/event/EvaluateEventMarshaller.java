package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.EVALUATE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EvaluateEventMarshaller implements ProcessEventMarshaller<EvaluateEvent> {

   @Override
   public EvaluateEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      Set<String> expand = new HashSet<String>();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String expression = input.readUTF();
      String thread = input.readUTF();
      boolean refresh = input.readBoolean();
      int count = input.readInt();
      
      for(int i = 0; i < count; i++) {
         String path = input.readUTF();
         expand.add(path);
      }
      return new EvaluateEvent.Builder(process)
         .withExpression(expression)
         .withRefresh(refresh)
         .withThread(thread)
         .withExpand(expand)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(EvaluateEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Set<String> expand = event.getExpand();
      String expression = event.getExpression();
      String process = event.getProcess();
      String thread = event.getThread();
      boolean refresh = event.isRefresh();
      int count = expand.size();
      
      output.writeUTF(process);
      output.writeUTF(expression);
      output.writeUTF(thread);
      output.writeBoolean(refresh);
      output.writeInt(count);
      
      for(String name : expand) {
         output.writeUTF(name);
      }
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(EVALUATE.code, array, 0, array.length);
   }
}