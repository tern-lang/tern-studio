package org.ternlang.studio.agent.cli;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class CommandLineBuilder {

   private static final String ILLEGAL_OPTION = "Unknown option '%s', option not supported";
   private static final String INVALID_VALUE = "Invalid value '%s' for '%s' should match pattern '%s'";

   private final List<? extends CommandOption> options;
   private final CommandOptionParser parser;
   private final String[] empty;

   public CommandLineBuilder(List<? extends CommandOption> options) {
      this.parser = new CommandOptionParser(options);
      this.empty = new String[]{};
      this.options = options;
   }

   public CommandLine build(String[] arguments) {
      return build(arguments, empty);
   }

   public CommandLine build(String[] arguments, String[] path) {
      Map<String, CommandOption> tokens = new LinkedHashMap<>();
      Map<String, Object> map = new LinkedHashMap<>();
      CommandFile file = new CommandFile(path);

      for (CommandOption option : options) {
         String name = option.getName();
         String code = option.getCode();

         if (tokens.put("--" + name, option) != null) {
            throw new IllegalStateException("Option '" + name + "' declared twice");
         }
         if (tokens.put("-" + code, option) != null) {
            throw new IllegalStateException("Option '" + code + "' declared twice");
         }
      }
      Deque<String> combined = file.combine(arguments);

      while(!combined.isEmpty()) {
         CommandValue value = next(combined, tokens);

         if(value == null) {
            break;
         }
         Object object = value.getValue();
         String name = value.getName();

         map.put(name, object);
      }
      return create(combined, map);
   }

   private CommandLine create(Deque<String> arguments, Map<String, Object> map) {
      for (CommandOption option : options) {
         String name = option.getName();

         if (!map.containsKey(name)) {
            Object token = option.getDefault();

            if (token != null) {
               Class type = option.getType();
               Object value = parser.convert(token, type);

               map.put(name, value);
            }
         }
      }
      String[] remainder = arguments.toArray(new String[]{});
      return new CommandLine(options, map, remainder);
   }

   private CommandValue next(Deque<String> arguments, Map<String, CommandOption> options) {
      while (!arguments.isEmpty()) {
         String name = arguments.poll().trim();
         CommandOption option = options.get(name);

         if (option == null) {
            if (name.startsWith("--") || name.startsWith("-")) {
               String warning = String.format(ILLEGAL_OPTION, name);
               Collection<CommandOption> values = options.values();

               CommandLineUsage.usage(values, warning);
            }
            arguments.offerFirst(name);
            return null;
         }
         String token = resolve(arguments, option);

         if (token == null) {
            Pattern pattern = option.getPattern();
            String warning = String.format(INVALID_VALUE, token, name, pattern);
            Collection<CommandOption> values = options.values();

            CommandLineUsage.usage(values, warning);
         }
         return parser.parse(option, token);
      }
      return null;
   }

   private String resolve(Deque<String> arguments, CommandOption option) {
      Class type = option.getType();

      if (!arguments.isEmpty()) {
         String token = arguments.peek();
         Pattern pattern = option.getPattern();

         if (pattern.matcher(token).matches()) {
            return arguments.poll();
         }
         Object object = option.getDefault();

         if (object != null) {
            return parser.interpolate(object);
         }
      }
      if (type == Boolean.class) {
         return parser.interpolate(Boolean.TRUE); // presence means true, e.g --version
      }
      return null;
   }
}
