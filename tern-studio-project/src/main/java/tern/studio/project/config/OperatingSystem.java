package tern.studio.project.config;

public enum OperatingSystem {
   WINDOWS("win64", "explorer \"%{resource}\"", "cmd /c \"cd %{resource}\" & start cmd"),
   MAC("mac", "open \"%{resource}\"", "open -a Terminal \"%{resource}\""),
   LINUX("linux64", "bash \"${resource}\"", "bash \"${resource}\"");

   private final String code;
   private final String explore;
   private final String terminal;

   private OperatingSystem(String code, String explore, String terminal) {
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
      
      for(OperatingSystem os : values) {
         if(token.startsWith(os.name().toLowerCase())) {
            return os;
         }
      }
      return WINDOWS;
   }
}
