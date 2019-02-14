package org.ternlang.studio.agent.event;

import static org.ternlang.studio.agent.event.ProcessEventType.BREAKPOINTS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BreakpointsEventMarshaller implements ProcessEventMarshaller<BreakpointsEvent> {

   @Override
   public BreakpointsEvent fromMessage(MessageEnvelope message) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      ByteArrayInputStream buffer = new ByteArrayInputStream(array, offset, length);
      DataInputStream input = new DataInputStream(buffer);
      Map<String, Map<Integer, Boolean>> breakpoints = new HashMap<String, Map<Integer, Boolean>>();
      String process = input.readUTF();
      int breakpointSize = input.readInt();
      
      for(int i = 0; i < breakpointSize; i++) {
         Map<Integer, Boolean> locations = new HashMap<Integer, Boolean>();
         String script = input.readUTF();
         int locationSize = input.readInt();

         for(int j = 0; j < locationSize; j++) {
            int line = input.readInt();
            boolean enable = input.readBoolean();
            
            locations.put(line, enable);
         }
         breakpoints.put(script, locations);
      }
      return new BreakpointsEvent.Builder(process)
         .withBreakpoints(breakpoints)
         .build();
   }

   @Override
   public MessageEnvelope toMessage(BreakpointsEvent event) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(buffer);
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      Set<String> scripts = breakpoints.keySet();
      String process = event.getProcess();
      int breakpointSize = breakpoints.size();
      
      output.writeUTF(process);
      output.writeInt(breakpointSize);
      
      for(String script : scripts) {
         Map<Integer, Boolean> locations = breakpoints.get(script);
         Set<Integer> lines = locations.keySet();
         int locationSize = locations.size();
         
         output.writeUTF(script);
         output.writeInt(locationSize);
         
         for(Integer line : lines) {
            Boolean enable = locations.get(line);
            
            output.writeInt(line);
            output.writeBoolean(enable);
         }
      }
      output.flush();
      byte[] array = buffer.toByteArray();
      return new MessageEnvelope(BREAKPOINTS.code, array, 0, array.length);
   }
}