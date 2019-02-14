package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.PROFILE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.ternlang.studio.agent.profiler.ProfileResult;

public class ProfileEventMarshaller implements ProcessEventMarshaller<ProfileEvent> {

   @Override
   public ProfileEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      Set<ProfileResult> results = new TreeSet<ProfileResult>();
      String process = input.readUTF();
      int resultSize = input.readInt();
      
      for(int i = 0; i < resultSize; i++) {
         ProfileResult result = new ProfileResult();
         String resource = input.readUTF();
         long time = input.readLong();
         int count = input.readInt();
         int line = input.readInt();
 
         result.setResource(resource);
         result.setCount(count);
         result.setLine(line);
         result.setTime(time);
         results.add(result);
      }
      return new ProfileEvent.Builder(process)
         .withResults(results)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(ProfileEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Set<ProfileResult> results = event.getResults();
      String process = event.getProcess();
      int resultSize = results.size();
      
      output.writeUTF(process);
      output.writeInt(resultSize);
      
      for(ProfileResult result : results) {
         String resource = result.getResource();
         int line = result.getLine();
         int count = result.getCount();
         long time = result.getTime();

         output.writeUTF(resource);
         output.writeLong(time);        
         output.writeInt(count);
         output.writeInt(line);
      }
      output.flush();
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(PROFILE.code, array, 0, array.length);
   }
}