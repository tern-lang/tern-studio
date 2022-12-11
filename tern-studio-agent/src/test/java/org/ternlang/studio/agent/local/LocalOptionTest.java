package org.ternlang.studio.agent.local;

import junit.framework.TestCase;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;

import java.io.File;

public class LocalOptionTest extends TestCase {

   public void testLocalOption1() {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(new String[]{
           "--version",
           "--verbose",
           "--directory=."
      });

      LocalCommandLine local = new LocalCommandLine(line);

      assertFalse(local.isCheck());
      assertTrue(local.isVersion());
      assertTrue(local.isDebug());
      assertEquals(local.getDirectory(), new File("."));
   }

   public void testLocalOption2() {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(new String[]{
           "--verbose",
           "--directory=."
      });

      LocalCommandLine local = new LocalCommandLine(line);

      assertFalse(local.isVersion());
      assertTrue(local.isDebug());
      assertEquals(local.getDirectory(), new File("."));
   }
}
