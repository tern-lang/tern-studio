package org.ternlang.studio.agent.local;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.ternlang.core.module.Path;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandOption;

public class LocalCommandLine {

   private final CommandLine line;
   
   public LocalCommandLine(CommandLine line) {
      this.line = line;
   }
   
   public List<? extends CommandOption> getOptions(){
      return line.getOptions();
   }
   
   public boolean isDebug() {
      return Boolean.TRUE.equals(line.getValue(LocalOption.VERBOSE.name));
   }
   
   public boolean isCheck() {
      return Boolean.TRUE.equals(line.getValue(LocalOption.CHECK.name));
   }
   
   public boolean isWait() {
      return Boolean.TRUE.equals(line.getValue(LocalOption.WAIT.name));
   }

   public boolean isVersion() {
      return Boolean.TRUE.equals(line.getValue(LocalOption.VERSION.name));
   }
   
   public Integer getPort() {
      return (Integer)line.getValue(LocalOption.PORT.name);
   }

   public File getDirectory() {
      return (File)line.getValue(LocalOption.DIRECTORY.name);
   }
   
   public List<File> getClasspath() {
      return (List<File>)line.getValue(LocalOption.CLASSPATH.name);
   }

   public String getEvaluation() {
      return (String)line.getValue(LocalOption.EXPRESSION.name);
   }

   public URI getNotifyURI() {
      return (URI)line.getValue(LocalOption.NOTIFY.name);
   }
   
   public URI getDownloadURI() {
      return (URI)line.getValue(LocalOption.URL.name);
   }

   public Path getScript() {
      return LocalCommandExtractor.getScript(line);
   }
   
   public String[] getArguments() {
      return LocalCommandExtractor.getArguments(line);
   }
}
