package org.ternlang.studio.service.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.Map;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Certificate;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportCursor;
import org.simpleframework.transport.TransportWriter;
import org.simpleframework.transport.trace.Trace;

public class StreamChannel implements Channel {
   
   private final TransportWriter writer;
   private final TransportCursor cursor;
   private final Transport transport;
   
   public StreamChannel(InputStream input, OutputStream output) {
      this.transport = new StreamTransport(input, output);
      this.cursor = new TransportCursor(transport);
      this.writer = new TransportWriter(transport);
   }

   @Override
   public boolean isSecure() {
      return false;
   }

   @Override
   public SocketChannel getSocket() {
      return null;
   }

   @Override
   public Certificate getCertificate() {
      return null;
   }

   @Override
   public Trace getTrace() {
      return null;
   }

   @Override
   public ByteCursor getCursor() {
      return cursor;
   }

   @Override
   public ByteWriter getWriter() {
      return writer;
   }

   @Override
   public Map getAttributes() {
      return null;
   }

   @Override
   public void close() {
      try {
         transport.close();
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
}
