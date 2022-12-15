package org.ternlang.studio.agent.cli;

import junit.framework.TestCase;
import org.ternlang.studio.agent.local.LocalOption;
import org.ternlang.studio.agent.worker.WorkerOption;

import java.io.File;

public class CommandLineTest extends TestCase {

   public void testLocalSingleOption() {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(new String[]{"--verbose"});

      assertEquals(line.getValue("verbose"), true);
   }

   public void testLocalOptions() {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(new String[]{"--directory", "/home/path", "--verbose", "--port", "9088", "foo.tern"});

      assertEquals(line.getValue("directory"), new File("/home/path"));
      assertEquals(line.getValue("verbose"), true);
      assertEquals(line.getValue("port"), 9088);
      assertEquals(line.getArguments().length, 1);
      assertEquals(line.getArguments()[0], "foo.tern");
   }

   public void testWorkerOptions() {
      CommandLineBuilder builder = WorkerOption.getBuilder();
      CommandLine line = builder.build(new String[]{"--host", "localhost", "--mode", "SCRIPT", "-l", "INFO"});

      assertEquals(line.getValue("host"), "localhost");
      assertEquals(line.getValue("mode"), "SCRIPT");
      assertEquals(line.getValue("level"), "INFO");
   }
}
