package tern.studio.agent.worker;

import tern.studio.agent.cli.CommandLine;
import tern.studio.agent.cli.CommandLineBuilder;

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
