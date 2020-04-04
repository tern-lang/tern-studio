package org.ternlang.studio.agent.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import junit.framework.TestCase;

import org.ternlang.agent.message.common.ExecuteStatus;
import org.ternlang.agent.message.common.ProcessMode;
import org.ternlang.agent.message.event.BeginEvent;
import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.common.thread.ThreadPool;
import org.ternlang.message.ArrayByteBuffer;
import org.ternlang.studio.agent.log.ConsoleLog;
import org.ternlang.studio.agent.log.Log;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.agent.log.LogLogger;

public class ProcessEventConnectionTest extends TestCase {

   public void testConnection() throws Exception {
      Log log = new ConsoleLog();
      LogLogger logger = new LogLogger(log, LogLevel.DEBUG);
      Executor executor = new ThreadPool();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      MessageEnvelopeWriter writer = new MessageEnvelopeWriter(buffer, buffer);
      ProcessEventCodec codec = new ProcessEventCodec();
      ArrayByteBuffer frame = new ArrayByteBuffer();
      
      codec.with(frame, 0, Integer.MAX_VALUE);      
      codec.begin()
         .process("process")
         .duration(1234)
         .threads(55)
         .usedMemory(3444)
         .status(ExecuteStatus.COMPILING)
         .mode(ProcessMode.REMOTE)
         .system("Windows 10")
         .pid("pid-11")
         .totalMemory(33333)
         .duration(60000)
         .resource()
             .some("/blah/some/resource.tern");         
      
      int length = frame.length();
      byte[] array = frame.getByteArray();
      MessageEnvelope envelope = new MessageEnvelope(0, array, 0, length);
      
      writer.write(envelope);
      buffer.flush();
      
      byte[] output = buffer.toByteArray();
      ByteArrayInputStream input = new ByteArrayInputStream(output);      
      ProcessEventConnection connection = new ProcessEventConnection(logger, executor, input, System.out, input);
      ProcessEventChecker checker = new ProcessEventChecker();
      
      
      checker.register(BeginEvent.class, new Consumer<BeginEvent>() {

         @Override
         public void accept(BeginEvent event) {
            assertEquals(event.duration(), 60000);
            assertEquals(event.threads(), 55);
            assertEquals(event.usedMemory(), 3444);
            assertEquals(event.resource().isSome(), true);
            assertEquals(event.resource().get().toString(), "/blah/some/resource.tern");
            assertEquals(event.system().toString(), "Windows 10");
            assertEquals(event.totalMemory(), 33333);
            assertEquals(event.pid().toString(), "pid-11");
            assertEquals(event.duration(), 60000);
            assertEquals(event.status(), ExecuteStatus.COMPILING);
            assertEquals(event.mode(), ProcessMode.REMOTE);
         }         
      });
      
      connection.getConsumer().consume(checker);
   }
}
