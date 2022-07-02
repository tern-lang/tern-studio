package org.ternlang.studio.core.agent.worker;

import static org.ternlang.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.RUN_PATH;
import static org.ternlang.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simpleframework.module.annotation.Component;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.worker.WorkerProcess;
import org.ternlang.studio.core.ProcessDefinition;
import org.ternlang.studio.core.ProcessLauncher;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.studio.project.Workspace;
import org.ternlang.studio.project.config.ProcessConfiguration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WorkerProcessLauncher implements ProcessLauncher {

   private final WorkerProcessNameFilter filter;
   private final Workspace workspace;

   public ProcessDefinition launch(ProcessConfiguration configuration) throws Exception {
      long timeout = workspace.getTimeLimit();
      int port = configuration.getPort();
      String host = configuration.getHost();
      String level = "DEBUG";//workspace.getLogger();
      String name = filter.generate();
      String mode = ProcessMode.SCRIPT.name();
      String javaHome = System.getProperty("java.home");
      File tempPath = HomeDirectory.getPath(TEMP_PATH);
      File jarFile = new File(tempPath, JAR_FILE);
      File runPath = HomeDirectory.getPath(RUN_PATH);
      String jarPath = jarFile.getCanonicalPath();
      String java = String.format("%s%sbin%sjava", javaHome, File.separatorChar, File.separatorChar);
      String classesUrl = String.format("http://%s:%s/class/", host, port);
      String policyUrl = String.format("http://%s:%s/policy", host, port);
      Map<String, String> variables = configuration.getVariables();
      List<String> arguments = configuration.getArguments();
      String className = WorkerProcess.class.getCanonicalName();
      List<String> command = new ArrayList<String>();

      command.add(java);
      command.add("-XX:+IgnoreUnrecognizedVMOptions");

      addOpens(command,
           "java.base",
           new String[]{
                "jdk.internal.loader",
                "java.lang",
                "java.security",
                "java.math",
                "java.text",
                "java.time",
                "java.time.zone",
                "java.io",
                "java.net",
                "java.nio",
                "java.nio.channels",
                "java.util",
                "java.util.regex",
                "java.util.function",
                "java.util.stream",
                "java.util.concurrent",
                "java.util.concurrent.atomic",
           });

      addOpens(command,
           "java.desktop",
           new String[]{
                "java.awt",
                "java.awt.color",
                "java.awt.event",
                "java.awt.image",
                "java.awt.font",
                "javax.sound.midi",
                "javax.sound.sampled",
                "javax.swing",
                "javax.swing.border",
                "javax.imageio"
           });

      command.addAll(arguments);

      if (workspace.isSecurityEnabled()) {
         command.add("-Djava.security.manager");
         command.add("-Djava.security.policy=" + policyUrl);
      }
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
      command.add("--timeout=" + timeout);

      ProcessBuilder builder = new ProcessBuilder(command);

      if (!variables.isEmpty()) {
         Map<String, String> environment = builder.environment();
         environment.putAll(variables);
      }

      log.info(name + ": " + command);
      runPath.mkdirs();
      builder.directory(runPath);
      builder.redirectErrorStream(true);

      Process process = builder.start();
      return new ProcessDefinition(process, name);
   }

   public static void addOpens(List<String> command, String module, String[] packages) {
      for (String entry : packages) {
         String value = String.format("--add-opens=%s/%s=ALL-UNNAMED", module.trim(), entry.trim());

         if (!command.contains(value)) {
            command.add(value);
         }
      }
   }
}