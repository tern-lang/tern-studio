package org.ternlang.studio.agent.worker;

import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;

public class WorkerProcess {

   public static void main(String[] arguments) throws Exception {
      launch(arguments);
   }

   public static void launch(String... options) throws Exception {
      CommandLineBuilder builder = WorkerOption.getBuilder();
      CommandLine line = builder.build(options);
      WorkerCommandLine local = new WorkerCommandLine(line);
      WorkerProcessExecutor executor = new WorkerProcessExecutor();

      executor.execute(local);
   }

}
