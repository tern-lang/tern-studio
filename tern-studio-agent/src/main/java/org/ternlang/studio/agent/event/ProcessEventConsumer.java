package org.ternlang.studio.agent.event;

import java.io.Closeable;
import java.io.InputStream;

import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.agent.message.event.ProcessEventHandler;
import org.ternlang.message.ArrayByteBuffer;

public class ProcessEventConsumer {

   private final MessageEnvelopeReader reader;

   public ProcessEventConsumer(InputStream stream, Closeable closeable) {
      this.reader = new MessageEnvelopeReader(stream, closeable);
   }
   
   public boolean consume(ProcessEventHandler handler) throws Exception {
      MessageEnvelope message = reader.read();
      int code = message.getCode();
      byte[] data = message.getData();
      int offset = message.getOffset();
      int length = message.getLength();

      if(length > 0) {
         ProcessEventCodec codec = new ProcessEventCodec(); // for the sake of concurrency
         ArrayByteBuffer frame = new ArrayByteBuffer();
         
         frame.wrap(data, offset, length);
         codec.with(frame, 0, Integer.MAX_VALUE);

         return codec.match(handler);
      }
      return false;
   }

}