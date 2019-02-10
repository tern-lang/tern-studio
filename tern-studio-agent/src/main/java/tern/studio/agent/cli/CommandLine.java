package tern.studio.agent.cli;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandLine {   
   
   private final List<? extends CommandOption> options;
   private final Map<String, Object> values;
   private final String[] arguments;
   
   public CommandLine(List<? extends CommandOption> options, Map<String, Object> values, String[] arguments) {
      this.options = Collections.unmodifiableList(options);
      this.values = Collections.unmodifiableMap(values);
      this.arguments = arguments;
   }
   
   public List<? extends CommandOption> getOptions(){
      return options;
   }

   public Map<String, Object> getValues() {
      return values;
   }
   
   public Object getValue(String name) {
      return values.get(name);
   }
   
   public String[] getArguments(){
      return arguments;
   }

}