package tern.studio.service.message;

import java.io.IOException;
import java.io.OutputStream;

import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;

public class ChannelOutputStream extends OutputStream {

   private final ByteWriter writer;
   private final Channel channel;
      
   public ChannelOutputStream(Channel channel) {
      this.writer = channel.getWriter();
      this.channel = channel;
   }

   @Override
   public void write(int octet) throws IOException {
      write(new byte[]{(byte)octet});
   }
   
   @Override
   public void write(byte[] array, int off, int size) throws IOException {
      writer.write(array, off, size);     
   }
   
   @Override
   public void flush() throws IOException { 
      writer.flush();
   }  

   @Override
   public void close() throws IOException {
      channel.close();    
   }
}