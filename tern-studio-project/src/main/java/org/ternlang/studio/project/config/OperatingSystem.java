package org.ternlang.studio.project.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

public enum OperatingSystem {
   WINDOWS("win64", "explorer \"%{resource}\"", "cmd /c \"cd %{resource}\" & start cmd", "${install.home}", "${user.home}"),
   MAC("mac", "open \"%{resource}\"", "open -a Terminal \"%{resource}\"", "${install.home}", "${user.home}"),
   LINUX("linux64", "bash \"${resource}\"", "bash \"${resource}\"", "${install.home}", "${user.home}");

   private final String code;
   private final String explore;
   private final String terminal;
   private final String install;
   private final String home;

   private OperatingSystem(String code, String explore, String terminal, String install, String home) {
      this.home = home;
      this.install = install;
      this.explore = explore;
      this.terminal = terminal;
      this.code = code;
   }

   public String getCode() {
      return code;
   }

   public boolean isWindows() {
      return this == WINDOWS;
   }

   public boolean isLinux() {
      return this == LINUX;
   }

   public boolean isMac() {
      return this == MAC;
   }

   public File getHomePath() {
      return interpolate(home, true);
   }

   public File getInstallPath() {
      return interpolate(install, false);
   }

   public String createExploreCommand(String resource) {
      return explore.replace("%{resource}", resource);
   }

   public String createTerminalCommand(String resource) {
      return terminal.replace("%{resource}", resource);
   }

   public static OperatingSystem resolveSystem() {
      OperatingSystem[] values = OperatingSystem.values();
      String system = System.getProperty("os.name");
      String token = system.toLowerCase();

      for (OperatingSystem os : values) {
         if (token.startsWith(os.name().toLowerCase())) {
            return os;
         }
      }
      return WINDOWS;
   }

   private static File interpolate(String path, boolean create) {
      Properties properties = System.getProperties();
      Enumeration<?> keys = properties.propertyNames();
      String original = path;

      while (keys.hasMoreElements()) {
         String name = String.valueOf(keys.nextElement());
         String value = properties.getProperty(name);

         if (value != null) {
            path = path.replace("${" + name + "}", value);
            path = path.replace("$" + name, value);
         }
      }
      File file = new File(path.trim());

      if(create) {
         file.mkdirs();
      }
      if (file.exists()) {
         return file;
      }
      throw new IllegalStateException("Could not resolve directory " + original);
   }
}
