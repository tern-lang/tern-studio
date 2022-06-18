package org.ternlang.studio.core;

import org.ternlang.core.module.Path;
import org.ternlang.studio.agent.cli.CommandLineBuilder;
import org.ternlang.studio.agent.cli.CommandOption;
import org.ternlang.studio.agent.log.LogLevel;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.studio.project.ProjectMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.EMPTY_LIST;

public enum StudioOption implements CommandOption {
   AGENT_POOL("n", "agent-pool", "Number of agents in pool", "\\d+", Integer.class, 4),
   PORT("p", "port", "Port for HTTP connections", "\\d+", Integer.class, 0),
   MODE("m", "mode", "Mode to start on", "(DEVELOP|DEBUG)", ProjectMode.class, ProjectMode.DEVELOP),
   DIRECTORY("d", "directory", "Directory used for sources", ".*", File.class,  HomeDirectory.getPath("work")),
   LOG_LEVEL("l", "log-level", "Level of logging", "(TRACE|DEBUG|INFO)", String.class, LogLevel.INFO),
   LOG("f", "log-file", "Log file to use", ".+", File.class, HomeDirectory.getPath("log/ternd.log")),
   SCRIPT("s", "script", "Script to launch", ".*.tern", Path.class),
   SERVER_ONLY("o", "server-only", "Launch server only", "(true|false)", Boolean.class, false),
   CLIENT_DEBUG("i", "client-debug", "Enable client debugger", "(true|false)", String.class, false); // firebug

   public final Pattern pattern;
   public final String description;
   public final Object value;
   public final String name;
   public final String code;
   public final Class type;

   private StudioOption(String code, String name, String description, String pattern, Class type) {
      this(code, name, description, pattern, type, null);
   }
   
   private StudioOption(String code, String name, String description, String pattern, Class type, Object value) {
      this.pattern = Pattern.compile(pattern);
      this.description = description;
      this.value = value;
      this.name = name;
      this.code = code;
      this.type = type;
   }
   
   public String getValue() {
      return System.getProperty(name);
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
      StudioOption[] options = StudioOption.values();
      
      if(options.length > 0) {
         List<StudioOption> list = new ArrayList<StudioOption>();
         CommandLineBuilder parser = new CommandLineBuilder(list);
         
         for(StudioOption option : options) {
            list.add(option);
         }
         return parser;
      }
      return new CommandLineBuilder(EMPTY_LIST);
   }
}