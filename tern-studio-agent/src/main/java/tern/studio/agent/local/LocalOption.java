package tern.studio.agent.local;

import static java.util.Collections.EMPTY_LIST;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import tern.core.module.Path;
import tern.studio.agent.cli.CommandLineBuilder;
import tern.studio.agent.cli.CommandOption;

public enum LocalOption implements CommandOption {
   DIRECTORY("d", "directory", "specify directory to execute in", ".+", File.class, "."),
   URL("u", "url", "specify a URL to download sources from", ".+", URI.class),
   SCRIPT("s", "script", "script to execute", ".+", Path.class),
   EXPRESSION("e", "expression", "expression to evaluate", ".+", String.class),
   CLASSPATH("cp", "classpath", "optional classpath file", ".+", File[].class),
   VERBOSE("v", "verbose", "enable verbose logging", "(true|false)", Boolean.class, false),
   CHECK("c", "check", "compile script only", "(true|false)", Boolean.class, false),
   PORT("p", "port", "debug port", "\\d+", Integer.class),
   WAIT("w", "wait", "wait for debugger", "(true|false)", Boolean.class, false),
   VERSION("ve", "version", "implementation version", ".+", Boolean.class, false);

   public final Pattern pattern;
   public final String description;
   public final Object value;
   public final String name;
   public final String code;
   public final Class type;

   private LocalOption(String code, String name, String description, String pattern, Class type) {
      this(code, name, description, pattern, type, null);
   }
   
   private LocalOption(String code, String name, String description, String pattern, Class type, Object value) {
      this.pattern = Pattern.compile(pattern);
      this.description = description;
      this.value = value;
      this.name = name;
      this.code = code;
      this.type = type;
   }

   @Override
   public String getCode() {
      return code;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public Object getDefault() {
      return value;
   }
   
   @Override
   public Pattern getPattern() {
      return pattern;
   }

   @Override
   public Class getType() {
      return type;
   }
   
   public static CommandLineBuilder getBuilder(){
      LocalOption[] options = LocalOption.values();
      
      if(options.length > 0) {
         List<LocalOption> list = new ArrayList<LocalOption>();
         CommandLineBuilder parser = new CommandLineBuilder(list);
         
         for(LocalOption option : options) {
            list.add(option);
         }
         return parser;
      }
      return new CommandLineBuilder(EMPTY_LIST);
   }
}
