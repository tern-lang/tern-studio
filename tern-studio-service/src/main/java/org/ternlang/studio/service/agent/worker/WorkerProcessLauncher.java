package org.ternlang.studio.service.agent.worker;

import static org.ternlang.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.worker.WorkerProcess;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.config.ProcessConfiguration;
import org.ternlang.studio.service.ProcessDefinition;
import org.ternlang.studio.service.ProcessLauncher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkerProcessLauncher implements ProcessLauncher {
   
   private final WorkerProcessNameFilter filter;
   private final Workspace workspace;
   
   public WorkerProcessLauncher(WorkerProcessNameFilter filter, Workspace workspace) {     
      this.workspace = workspace;
      this.filter = filter;
   }

   public ProcessDefinition launch(ProcessConfiguration configuration) throws Exception {
      int port = configuration.getPort();
      String host = configuration.getHost();
      String level = "DEBUG";//workspace.getLogger();
      String name = filter.generate();
      String mode = ProcessMode.SCRIPT.name();
      String javaHome = System.getProperty("java.home");
      File directory = HomeDirectory.getPath(TEMP_PATH);
      File jarFile = new File(directory, JAR_FILE);
      String jarPath = jarFile.getCanonicalPath();
      String java = String.format("%s%sbin%sjava", javaHome, File.separatorChar, File.separatorChar);
      String classesUrl = String.format("http://%s:%s/class/", host, port);
      Map<String, String> variables = configuration.getVariables();
      List<String> arguments = configuration.getArguments();
      String className = WorkerProcess.class.getCanonicalName();
      List<String> command = new ArrayList<String>();
      
      command.add(java);
      command.addAll(arguments);
      command.add("-jar");
      command.add(jarPath);
      command.add(classesUrl);
      command.add(className);
      command.add("org.ternlang.");
      
      command.add("--host=" + host);
      command.add("--port=" + port);
      command.add("--name=" + name);
      command.add("--level=" + level);
      command.add("--mode=" + mode);

      ProcessBuilder builder = new ProcessBuilder(command);
      
      if(!variables.isEmpty()) {
         Map<String, String> environment = builder.environment();
         environment.putAll(variables);
      }
      
      log.info(name + ": " +command);
      builder.directory(directory);
      builder.redirectErrorStream(true);
      
      Process process = builder.start();
      return new ProcessDefinition(process, name);
   }
}