package tern.studio.agent.event;

import java.io.Closeable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProcessEventConsumer {
   
   private final Map<Integer, ProcessEventMarshaller> marshallers;
   private final MessageEnvelopeReader reader;

   public ProcessEventConsumer(InputStream stream, Closeable closeable) {
      this.marshallers = new HashMap<Integer, ProcessEventMarshaller>();
      this.reader = new MessageEnvelopeReader(stream, closeable);
   }
   
   public ProcessEvent consume() throws Exception {
      if(marshallers.isEmpty()) {
         ProcessEventType[] events = ProcessEventType.values();
         
         for(ProcessEventType event : events) {
            ProcessEventMarshaller marshaller = event.marshaller.newInstance();
            marshallers.put(event.code, marshaller);
         }
      }
      MessageEnvelope message = reader.read();
      int code = message.getCode();
      ProcessEventMarshaller marshaller = marshallers.get(code);
      
      if(marshaller == null) {
         throw new IllegalStateException("Could not find marshaller for " + code);
      }
      return marshaller.fromMessage(message);
   }

}