package org.ternlang.studio.agent.local;

import org.ternlang.core.module.Path;
import org.ternlang.studio.agent.cli.CommandLine;

public class LocalCommandExtractor {

   public static Path getScript(CommandLine line) {
      String[] arguments = line.getArguments();
      Path script = (Path)line.getValue(LocalOption.SCRIPT.name);;

      if(script == null && arguments.length > 0) {
         return new Path(arguments[0]);
      }
      return script;
   }

   public static String[] getArguments(CommandLine line) {
      String[] arguments = line.getArguments();
      Path script = (Path)line.getValue(LocalOption.SCRIPT.name);;

      if(script == null && arguments.length > 0) {
         String[] remainder = new String[arguments.length - 1];

         for(int i = 0; i < remainder.length; i++) {
            remainder[i] = arguments[i + 1];
         }
         return remainder;
      }
      return arguments;
   }
}
