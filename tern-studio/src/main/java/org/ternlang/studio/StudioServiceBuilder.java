package org.ternlang.studio;

import lombok.extern.slf4j.Slf4j;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;
import org.ternlang.studio.agent.runtime.MainClassValue;
import org.ternlang.studio.agent.runtime.ManifestLocator;
import org.ternlang.studio.agent.runtime.VersionValue;
import org.ternlang.studio.core.StudioCommandLine;
import org.ternlang.studio.core.StudioOption;
import org.ternlang.studio.core.splash.SplashScreen;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.ui.OperatingSystem;
import org.ternlang.ui.chrome.install.deploy.DeploymentManager;
import org.ternlang.ui.chrome.install.deploy.DeploymentTask;

import javax.swing.*;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

@Slf4j
public class StudioServiceBuilder {

   private static final String ABOUT_NAME = "Tern Develop";
   private static final String ALLOW_FORK_PROPERTY = "org.ternlang.studio.allowFork";
   private static final String DEPLOY_PATH = ".tern";

   private final ServiceType type;
   private final String[] arguments;

   public StudioServiceBuilder(String[] arguments) {
      this(arguments, ServiceType.IDE);
   }

   public StudioServiceBuilder(String[] arguments, ServiceType type) {
      this.arguments = arguments;
      this.type = type;
   }

   public StudioService create() throws Exception {
      CommandLineBuilder builder = StudioOption.getBuilder();
      CommandLine local = builder.build(arguments, type.getFiles());
      StudioCommandLine line = new StudioCommandLine(local);
      Map<String, Object> commands = local.getValues();
      Set<String> names = commands.keySet();
      Manifest manifest = ManifestLocator.getManifestFile(MainClassValue.APPLICATION_CLASS);
      String mainClass = type.type.getName();
      String version = VersionValue.getValue(manifest);
      String forkProperty = System.getProperty(ALLOW_FORK_PROPERTY, "true");
      String disableFork = String.format("-D%s=%s", ALLOW_FORK_PROPERTY, "false");
      DeploymentTask task = DeploymentManager.deploy(DEPLOY_PATH, mainClass, arguments, disableFork);
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
      Map<String, String> environment = System.getenv();
      Set<Map.Entry<String, String>> variables = environment.entrySet();

      for (Map.Entry<String, String> variable : variables) {
         String name = variable.getKey();
         String value = variable.getValue();

         System.out.println(name + "=" + value);
      }
      if (line.isServerOnly()) {
         System.setProperty("java.awt.headless", "true");
      } else {
         if (isForkDisabled) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", ABOUT_NAME);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SplashScreen.getPanel().show(60000); // 1 minute
            SplashScreen.getPanel().update("Tern Studio " + version);
         }
      }
      if (!isForkDisabled) {
         runnable.run();
      }
      return StudioService.builder()
           .commandLine(line)
           .operatingSystem(operatingSystem)
           .previouslyDeployed(previouslyDeployed)
           .mainClass(mainClass)
           .version(version)
           .build();

   }


   public static enum ServiceType {
      IDE(StudioApplication.class, "ternd.ini", "tern-studio.ini"),
      TERM(StudioTerminal.class, "term.ini", "terminal.ini");

      private final String[] files;
      private final Class type;

      private ServiceType(Class type, String... files) {
         this.type = type;
         this.files = files;
      }

      public String[] getFiles() {
         String[] paths = new String[files.length];

         for (int i = 0; i < files.length; i++) {
            paths[i] = getFile(files[i]);
         }
         return paths;
      }

      private static String getFile(String path) {
         File filePath = HomeDirectory.getHomeChildPath(path);

         if(!filePath.exists()) {
            filePath = HomeDirectory.getInstallChildPath(path);
         }
         if(filePath.exists()) {
            return filePath.getAbsolutePath();
         }
         return path;
      }
   }
}
