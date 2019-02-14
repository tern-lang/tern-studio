package org.ternlang.studio.agent.event;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageEnvelopeReader {

   public final DataInputStream stream;
   public final Closeable closeable;
   
   public MessageEnvelopeReader(InputStream stream, Closeable closeable) {
      this.stream = new DataInputStream(stream);
      this.closeable = closeable;
   }
   
   public synchronized MessageEnvelope read() throws IOException {
      return read(stream);
   }
   
   public static MessageEnvelope read(DataInput input) throws IOException {
      int length = input.readInt();
      int type = input.readInt();
      long expect = input.readLong();
      byte[] array = new byte[length];
      
      try {
         input.readFully(array);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read message of type " + type + " with length " + length, e);
      }
      long check  = MessageChecker.check(array, 0, length);
      
      if(check != expect) {
         throw new IllegalStateException("Message of type " + type + " did not match checksum " + check);
      }
      return new MessageEnvelope(type, array, 0, length);
   }
   
   public synchronized void close() throws IOException {
      closeable.close();
   }
}