package tern.studio.agent.worker;

import java.net.URI;

import tern.studio.agent.ProcessMode;
import tern.studio.agent.cli.CommandLine;
import tern.studio.agent.log.LogLevel;

public class WorkerCommandLine {
   
   public static final String DOWNLOAD_PATH = "/download";
   public static final String CLASSES_PATH = "/classes";

   private final CommandLine line;
   
   public WorkerCommandLine(CommandLine line) {
      this.line = line;
   }
   
   public String getName() {
      return (String)line.getValue(WorkerOption.NAME.name);
   }

   public LogLevel getLogLevel() {
      return LogLevel.resolveLevel((String)line.getValue(WorkerOption.LEVEL.name));
   }
   
   public ProcessMode getMode() {
      return ProcessMode.resolveMode((String)line.getValue(WorkerOption.MODE.name));
   }
   
   public String getAddress() {
      return "http://" + line.getValue(WorkerOption.HOST.name) + ":" + line.getValue(WorkerOption.PORT.name);
   }
   
   public URI getDownloadURL() {
      return URI.create(getAddress() + DOWNLOAD_PATH);
   }
   
   public URI getClassURL() {
      return URI.create(getAddress() + CLASSES_PATH);
   }

   public String[] getArguments() {
      return line.getArguments();
   }
}
