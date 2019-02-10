package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.FAULT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import tern.studio.agent.debug.ScopeVariableTree;

public class FaultEventMarshaller implements ProcessEventMarshaller<FaultEvent> {
   
   private final MapMarshaller marshaller;
   
   public FaultEventMarshaller() {
      this.marshaller = new MapMarshaller();
   }

   @Override
   public FaultEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      String process = input.readUTF();
      String resource = input.readUTF();
      String thread = input.readUTF();
      String cause = input.readUTF();
      int line = input.readInt();
      int change = input.readInt();
      Map<String, Map<String, String>> local = marshaller.readMap(input);
      Map<String, Map<String, String>> evaluation = marshaller.readMap(input);
      
      ScopeVariableTree tree = new ScopeVariableTree.Builder(change)
         .withLocal(local)
         .withEvaluation(evaluation)
         .build();
     
      return new FaultEvent.Builder(process)
         .withVariables(tree)
         .withThread(thread)
         .withCause(cause)
         .withResource(resource)
         .withLine(line)
         .build();
      
   }

   @Override
   public MessageEnvelope toMessage(FaultEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      ScopeVariableTree tree = event.getVariables();
      Map<String, Map<String, String>> local = tree.getLocal();
      Map<String, Map<String, String>> evaluation = tree.getEvaluation();
      String process = event.getProcess();
      String resource = event.getResource();
      String thread = event.getThread();
      String cause = event.getCause();
      int change = tree.getChange();
      int line = event.getLine();
      
      output.writeUTF(process);
      output.writeUTF(resource);
      output.writeUTF(thread);
      output.writeUTF(cause);
      output.writeInt(line);
      output.writeInt(change);
      marshaller.writeMap(output, local);
      marshaller.writeMap(output, evaluation);
      output.flush();
      
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(FAULT.code, array, 0, array.length);
   }
}