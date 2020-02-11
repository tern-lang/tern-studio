package org.ternlang.studio.agent.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.ternlang.studio.agent.event.ProcessEventChannel;

public class ProcessOutputStream extends OutputStream {

   private final ByteArrayOutputStream buffer;
   private final ProcessEventChannel channel;
   private final ProcessOutputType type;
   private final PrintStream stream;
   private final String process;
   
   public ProcessOutputStream(ProcessOutputType type, ProcessEventChannel channel, PrintStream stream, String process) {
      this.buffer = new ByteArrayOutputStream();
      this.process = process;
      this.channel = channel;
      this.stream = stream;
      this.type = type;
   }
   
   @Override
   public void write(int octet) throws IOException {
      write(new byte[]{(byte)octet});
   }
   
   @Override
   public void write(byte[] octets) throws IOException {
      write(octets, 0, octets.length);
   }
   
   @Override
   public void write(byte[] octets, int offset, int length) throws IOException {
      try {
         buffer.write(octets, offset, length);
         stream.write(octets, offset, length);
      }catch(Exception e) {
         throw new IOException("Error sending write event");
      }
   }
   
   @Override
   public void flush() throws IOException {
      try {
         byte[] octets = buffer.toByteArray();
         
         if(type == ProcessOutputType.STDERR) {
            channel.begin()
               .writeError()
               .process(process)
               .offset(0)
               .length(octets.length)
               .data().set(0, octets);
   
            channel.sendAsync();
            stream.flush();
         } else {
            channel.begin()
               .writeOutput()
               .process(process)
               .offset(0)
               .length(octets.length)
               .data().set(0, octets);

            channel.sendAsync();
            stream.flush();
         }
      }catch(Exception e) {
         throw new IOException("Error sending write event");
      } finally {
         buffer.reset();
      }
   }
   
}