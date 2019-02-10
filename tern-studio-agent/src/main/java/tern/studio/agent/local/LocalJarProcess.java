package tern.studio.agent.local;

import java.util.ArrayList;
import java.util.List;

import tern.core.module.Path;
import tern.studio.agent.cli.CommandLine;
import tern.studio.agent.cli.CommandLineBuilder;
import tern.studio.agent.runtime.RuntimeAttribute;

public class LocalJarProcess {
   
   public static final String MAIN_SCRIPT = "Main-Script";
   
   public static void main(String[] arguments) throws Exception {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(arguments);
      LocalCommandLine local = new LocalCommandLine(line);
      Path path = local.getScript();
      
      if(path == null) {
         String[] empty = new String[]{};
         List<String> expanded = new ArrayList<String>();
         String script = RuntimeAttribute.SCRIPT.getValue();
         
         for(String argument : arguments) {
            expanded.add(argument);
         }
         String argument = String.format("--%s=%s", LocalOption.SCRIPT.name, script);
         expanded.add(argument);
         arguments = expanded.toArray(empty);
      }
      LocalProcess.main(arguments);
   }
}
