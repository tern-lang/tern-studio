package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.SCOPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.ternlang.studio.agent.debug.ScopeVariableTree;
import org.ternlang.studio.agent.debug.ThreadStatus;

public class ScopeEventMarshaller implements ProcessEventMarshaller<ScopeEvent> {
   
   private final MapMarshaller marshaller;
   
   public ScopeEventMarshaller() {
      this.marshaller = new MapMarshaller();
   }

   @Override
   public ScopeEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String thread = input.readUTF();
      String stack = input.readUTF();
      String instruction = input.readUTF();
      String status = input.readUTF();
      String resource = input.readUTF();
      String source = null;
      
      if(input.readBoolean()) {
         source = input.readUTF();
      }
      int line = input.readInt();
      int depth = input.readInt();
      int sequence = input.readInt();
      int change = input.readInt();
      Map<String, Map<String, String>> local = marshaller.readMap(input);
      Map<String, Map<String, String>> evaluation = marshaller.readMap(input);
      
      ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
         .withLocal(local)
         .withEvaluation(evaluation)
         .build();
     
      return new ScopeEvent.Builder(process)
         .withVariables(tree)
         .withThread(thread)
         .withStack(stack)
         .withInstruction(instruction)
         .withStatus(ThreadStatus.valueOf(status))
         .withResource(resource)
         .withSource(source)
         .withLine(line)
         .withDepth(depth)
         .withKey(sequence)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(ScopeEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      ScopeVariableTree tree = event.getVariables();
      Map<String, Map<String, String>> local = tree.getLocal();
      Map<String, Map<String, String>> evaluation = tree.getEvaluation();
      String process = event.getProcess();
      String thread = event.getThread();
      String stack = event.getStack();
      String instruction = event.getInstruction();
      ThreadStatus status = event.getStatus();
      String resource = event.getResource();
      String source = event.getSource();
      int change = tree.getChange();
      int sequence = event.getKey();
      int line = event.getLine();
      int depth = event.getDepth();
      
      output.writeUTF(process);
      output.writeUTF(thread);
      output.writeUTF(stack);
      output.writeUTF(instruction);
      output.writeUTF(status.name());
      output.writeUTF(resource);
      
      if(source != null) {
         output.writeBoolean(true);
         output.writeUTF(source);
      } else {
         output.writeBoolean(false);
      }
      output.writeInt(line);
      output.writeInt(depth);
      output.writeInt(sequence);
      output.writeInt(change);
      marshaller.writeMap(output, local);
      marshaller.writeMap(output, evaluation);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(SCOPE.code, array, 0, array.length);
   }
}