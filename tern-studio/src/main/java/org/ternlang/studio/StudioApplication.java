package org.ternlang.studio;

import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

import javax.swing.UIManager;

import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.module.resource.Application;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;
import org.ternlang.studio.agent.runtime.MainClassValue;
import org.ternlang.studio.agent.runtime.ManifestLocator;
import org.ternlang.studio.agent.runtime.VersionValue;
import org.ternlang.studio.core.SplashScreen;
import org.ternlang.studio.core.StudioCommandLine;
import org.ternlang.studio.core.StudioOption;
import org.ternlang.ui.OperatingSystem;
import org.ternlang.ui.chrome.install.deploy.DeploymentManager;
import org.ternlang.ui.chrome.install.deploy.DeploymentTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Module
@Import(StudioApplication.class)
public class StudioApplication {

   private static final String ABOUT_NAME = "Tern Develop";
   private static final String ALLOW_FORK_PROPERTY = "org.ternlang.studio.allowFork";
   private static final String DEPLOY_PATH = ".tern";
   private static final String[] SEARCH_FILES = {
      "ternd.ini",
      "tern-studio.ini"
   };

   public static void main(String[] list) throws Exception {
      StudioProcess process = buildProcess(list);
      StudioCommandLine commandLine = process.getCommandLine();
      Runnable forkTask = process.getForkTask();
      String version = process.getVersion();
      int port = commandLine.getPort();

      if (commandLine.isServerOnly()) {
         System.setProperty("java.awt.headless", "true");
         Application.start(port, StudioApplication.class);
      } else {
         if (process.isForkRequired()) {
            forkTask.run();
         } else {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", ABOUT_NAME);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SplashScreen.getPanel().show(60000); // 1 minute
            SplashScreen.getPanel().update("Tern Studio " + version);
            Application.start(port, StudioApplication.class);
         }
      }
   }

   private static StudioProcess buildProcess(String[] list) {
      CommandLineBuilder builder = StudioOption.getBuilder();
      CommandLine local = builder.build(list, SEARCH_FILES);
      StudioCommandLine line = new StudioCommandLine(local);
      Map<String, Object> commands = local.getValues();
      Set<String> names = commands.keySet();
      Manifest manifest = ManifestLocator.getManifestFile(MainClassValue.APPLICATION_CLASS);
      String mainClass = StudioApplication.class.getName();
      String version = VersionValue.getValue(manifest);
      String forkProperty = System.getProperty(ALLOW_FORK_PROPERTY, "true");
      String disableFork = String.format("-D%s=%s", ALLOW_FORK_PROPERTY, "false");
      DeploymentTask task = DeploymentManager.deploy(DEPLOY_PATH, mainClass, list, disableFork);
      OperatingSystem operatingSystem = task.getOperatingSystem();
      Runnable runnable = task.getForkTask();
      boolean previouslyDeployed = task.isAlreadyDeployed();
      boolean isForkDisabled = forkProperty.equals("false");

      for (String name : names) {
         Object value = commands.get(name);
         String token = String.valueOf(value);

         System.out.println("--" + name + "=" + token);
         System.setProperty(name, token); // make available to configuration
      }
      return StudioProcess.builder()
            .commandLine(line)
            .operatingSystem(operatingSystem)
            .previouslyDeployed(previouslyDeployed)
            .mainClass(mainClass)
            .version(version)
            .forkTask(isForkDisabled ? null : runnable)
            .build();

   }

   @Data
   @Builder
   @AllArgsConstructor
   private static class StudioProcess {

      private final StudioCommandLine commandLine;
      private final OperatingSystem operatingSystem;
      private final String mainClass;
      private final String version;
      private final Runnable forkTask;
      private final boolean previouslyDeployed;

      public boolean isForkRequired() {
         return forkTask != null;
      }
   }
}
