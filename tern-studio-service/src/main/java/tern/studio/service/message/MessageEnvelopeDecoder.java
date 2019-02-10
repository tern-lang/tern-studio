package tern.studio.service.message;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import tern.studio.agent.event.MessageEnvelope;
import tern.studio.agent.event.MessageEnvelopeReader;

public class MessageEnvelopeDecoder {

   public static int decodeInt(byte[] data, int off, int length) throws IOException {
      InputStream stream = new ByteArrayInputStream(data, off, length);
      DataInput input = new DataInputStream(stream);
      
      return input.readInt();
   }
   
   public static MessageEnvelope decodeMessage(byte[] data, int off, int length) throws IOException {
      InputStream stream = new ByteArrayInputStream(data, off, length);
      DataInput input = new DataInputStream(stream);
      
      return MessageEnvelopeReader.read(input);
   }
}