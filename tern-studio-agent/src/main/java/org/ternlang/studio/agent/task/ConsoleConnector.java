package org.ternlang.studio.agent.task;

import org.ternlang.studio.agent.client.ConnectionListener;
import org.ternlang.studio.agent.event.ProcessEventChannel;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.ternlang.studio.agent.task.ProcessOutputType.STDERR;
import static org.ternlang.studio.agent.task.ProcessOutputType.STDOUT;

public class ConsoleConnector implements ConnectionListener{
   
   private final ProcessOutputStream errorAdapter;
   private final ProcessOutputStream outputAdapter;
   private final PrintStream originalOutput;
   private final PrintStream originalError;
   private final PrintStream output;
   private final PrintStream error;
   
   public ConsoleConnector(ProcessEventChannel channel, String process) throws Exception {
      this.errorAdapter = new ProcessOutputStream(STDERR, channel, System.err, process);
      this.outputAdapter = new ProcessOutputStream(STDOUT, channel, System.out, process);
      this.output = new ConsoleStream(outputAdapter, System.out, true, "UTF-8");
      this.error = new ConsoleStream(errorAdapter, System.err, true, "UTF-8");
      this.originalError = System.err;
      this.originalOutput = System.out;
   }

   public void connect() {
      try {
         // redirect all output to the streams
        // System.setOut(output);
         //System.setErr(error);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.buildAll(e));
      }
   }
   
   @Override
   public void onClose() {
      try {
       //  System.setOut(originalOutput);
       //  System.setErr(originalError);
      }catch(Exception e) {
         System.err.println(ExceptionBuilder.buildAll(e));
      }
   }
   
   private static class ConsoleStream extends PrintStream {
      
      private final PrintStream stream;
      
      public ConsoleStream(OutputStream out, PrintStream stream, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
         super(out, autoFlush, encoding);
         this.stream = stream;
      }

      @Override
      public void close(){
         stream.close(); // do not allow android to close event stream
      } 
   }
}