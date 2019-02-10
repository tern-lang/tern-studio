package tern.studio.agent.local;

import java.util.UUID;

public class LocalNameGenerator {

   private static final String PROCESS_PREFIX = "process";
   
   public static String getProcess() {
      return String.format("%s-%s", PROCESS_PREFIX, getProcessId());
   }
   
   private static String getProcessId() {
      try {
         return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
      }catch(Exception e) {
         return UUID.randomUUID().toString();
      }
   }
}
