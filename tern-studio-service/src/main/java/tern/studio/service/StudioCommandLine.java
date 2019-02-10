package tern.studio.service;

import java.util.List;

import tern.studio.agent.cli.CommandLine;
import tern.studio.agent.cli.CommandOption;
import tern.studio.project.ProjectMode;

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
