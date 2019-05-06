package org.ternlang.studio.agent.local;

import java.util.ArrayList;
import java.util.List;

import org.ternlang.core.module.Path;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;
import org.ternlang.studio.agent.runtime.RuntimeAttribute;

public class LocalJarProcess {
   
   public static void main(String[] arguments) throws Exception {
      CommandLineBuilder builder = LocalOption.getBuilder();
      CommandLine line = builder.build(arguments);
      LocalCommandLine local = new LocalCommandLine(line);
      Path path = local.getScript();
      
      if(path == null) {
         String[] empty = new String[]{};
         List<String> expanded = new ArrayList<String>();
         String script = RuntimeAttribute.MAIN_SCRIPT.getValue();
         
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
