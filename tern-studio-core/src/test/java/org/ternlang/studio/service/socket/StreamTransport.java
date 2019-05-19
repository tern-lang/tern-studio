package org.ternlang.studio.service.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import org.simpleframework.transport.Certificate;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.trace.Trace;

public class StreamTransport implements Transport {
   
   private final WritableByteChannel write;
   private final ReadableByteChannel read;
   private final OutputStream out;
   
   public StreamTransport(InputStream in, OutputStream out) {
      this.write = Channels.newChannel(out);
      this.read = Channels.newChannel(in);
      this.out = out;
   }
   
   public String getProtocol() {
      return null;
   }

   @Override
   public void close() throws IOException {
      write.close();
      read.close();
   }

   @Override
   public void flush() throws IOException {
      out.flush();
   }

   @Override
   public int read(ByteBuffer buffer) throws IOException {
      return read.read(buffer);
   }

   @Override
   public void write(ByteBuffer buffer) throws IOException {
      write.write(buffer);
   }

   @Override
   public Map getAttributes() {
      return null;
   }

   @Override
   public SocketChannel getChannel() {
      return null;
   }   

   @Override
   public SSLEngine getEngine() {
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
}

