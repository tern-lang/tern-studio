package tern.studio.agent.local;

import static tern.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.io.File;
import java.util.List;

import tern.studio.agent.cli.CommandLine;
import tern.studio.agent.cli.CommandLineBuilder;
import tern.studio.agent.cli.CommandLineUsage;
import tern.studio.agent.cli.CommandOption;
import tern.studio.agent.core.ClassPathUpdater;

public class LocalProcess {
   
   private static final String WARNING = "Could not find classpath entry %s";

   public static void main(String[] arguments) throws Exception {
      launch(arguments);
   }

   public static void launch(String... arguments) throws Exception {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(arguments);
      List<? extends CommandOption> options = line.getOptions();
      LocalCommandLine local = new LocalCommandLine(line);
      LocalProcessExecutor executor = new LocalProcessExecutor();
      List<File> classpath = local.getClasspath();
      boolean debug = local.isDebug();

      if(local.isVersion()) {
         String version = VERSION.getValue();

         System.err.println(version);
         System.err.flush();
         System.exit(0);
      }

      try {
         if(classpath != null) {
            for(File dependency : classpath) {
               if(!dependency.exists()) {
                  String path = dependency.getCanonicalPath();
                  String warning = String.format(WARNING, path);

                  CommandLineUsage.usage(options, warning);
               }
            }
            ClassPathUpdater.updateClassPath(classpath, debug);
         }
      }catch(Exception cause) {
         String message = cause.getMessage();
         CommandLineUsage.usage(options, message);
      }
      executor.execute(local);
   }

}