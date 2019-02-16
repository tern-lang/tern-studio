package org.ternlang.studio;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import javax.swing.*;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ternlang.studio.agent.cli.CommandLine;
import org.ternlang.studio.agent.cli.CommandLineBuilder;
import org.ternlang.studio.service.SplashScreen;
import org.ternlang.studio.service.StudioCommandLine;
import org.ternlang.studio.service.StudioOption;

@SpringBootApplication
public class StudioApplication {

   private static final String ABOUT_NAME = "Tern Develop";
   private static final String[] SEARCH_FILES = {
      "ternd.ini",
      "tern-studio.ini"
   };

   public static void main(String[] list) throws Exception {
      CommandLineBuilder builder = StudioOption.getBuilder();
      CommandLine local = builder.build(list, SEARCH_FILES);
      StudioCommandLine line = new StudioCommandLine(local);
      Map<String, Object> commands = local.getValues();
      Set<String> names = commands.keySet();
      String version = VERSION.getValue();

      //ThreadMonitor.start(5000);
      
      for(String name : names) {
         Object value = commands.get(name);
         String token = String.valueOf(value);
         
         System.out.println("--" + name + "=" + token);
         System.setProperty(name, token); // make available to configuration
      }
      if(line.isServerOnly()) {
         System.setProperty("java.awt.headless", "true");
      } else {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", ABOUT_NAME);
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         SplashScreen.getPanel().show(60000); // 1 minute
         SplashScreen.getPanel().update("Tern Studio " + version);
      }
      SpringApplication.run(StudioApplication.class, list);
      
   }
}