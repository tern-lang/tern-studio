package org.ternlang.studio.agent.event;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MessageEnvelopeWriter {

   private final DataOutputStream stream;
   private final Closeable closeable;
   
   public MessageEnvelopeWriter(OutputStream stream, Closeable closeable) {
      this.stream = new DataOutputStream(stream);
      this.closeable = closeable;
   }
   
   public synchronized void write(MessageEnvelope message) throws IOException {
      write(message, stream);
      stream.flush();
   }
   
   public static void write(MessageEnvelope message, DataOutput output) throws IOException {
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      int type = message.getCode();
      long check = MessageChecker.check(array, offset, length);
      
      output.writeInt(length); // length of the payload
      output.writeInt(type);
      output.writeLong(check);
      output.write(array, offset, length);
   }
   
   public synchronized void close() throws IOException {
      try {
         stream.flush();
      }finally {
         closeable.close();
      }
   }
}