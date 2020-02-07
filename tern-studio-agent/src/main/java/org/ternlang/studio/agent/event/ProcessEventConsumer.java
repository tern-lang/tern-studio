package org.ternlang.studio.agent.event;

import java.io.Closeable;
import java.io.InputStream;

import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.agent.message.event.ProcessEventHandler;
import org.ternlang.message.ByteArrayFrame;

public class ProcessEventConsumer {

   private final MessageEnvelopeReader reader;
   private final ProcessEventCodec codec;
   private final ByteArrayFrame frame;

   public ProcessEventConsumer(InputStream stream, Closeable closeable) {
      this.reader = new MessageEnvelopeReader(stream, closeable);
      this.codec = new ProcessEventCodec();
      this.frame = new ByteArrayFrame();
   }
   
   public boolean consume(ProcessEventHandler handler) throws Exception {
      MessageEnvelope message = reader.read();
      int code = message.getCode();
      byte[] data = message.getData();
      int offset = message.getOffset();
      int length = message.getLength();

      frame.wrap(data, offset, length);

      return codec.match(handler);
   }

}