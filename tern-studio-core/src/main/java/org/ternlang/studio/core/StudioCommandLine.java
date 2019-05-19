package org.ternlang.studio.core;

import java.util.List;

import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandOption;
import org.ternlang.studio.project.ProjectMode;

public class StudioCommandLine {

   private final CommandLine line;
   
   public StudioCommandLine(CommandLine line) {
      this.line = line;
   }
   
   public List<? extends CommandOption> getOptions(){
      return line.getOptions();
   }
   
   public boolean isServerOnly(){
      return (Boolean)line.getValue(StudioOption.SERVER_ONLY.name);
   }
   
   public ProjectMode getProjectMode() {
      return (ProjectMode)line.getValue(StudioOption.MODE.name);
   }

   public Integer getPort() {
      return (Integer)line.getValue(StudioOption.PORT.name);
   }

   public String[] getArguments() {
      return line.getArguments();
   }
}
