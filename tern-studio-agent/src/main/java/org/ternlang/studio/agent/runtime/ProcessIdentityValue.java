package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.PID;

import java.lang.reflect.Method;

public class ProcessIdentityValue implements RuntimeValue {

   public String getName() {
      return PID.name;
   }

   @Override
   public String getValue() {
      String pid = getFromProcess();

      if (pid == null) {
         pid = getFromRuntimeMXBean();
      }
      if (pid == null) {
         pid = getFromManagementFactory();
      }
      if (pid == null) {
         pid = getFromDefault();
      }
      return pid;
   }

   private static String getFromDefault() {
      return "-";
   }

   private static String getFromProcess() { // Andrioid
      try {
         Class process = Class.forName("android.os.Process");
         Method pidMethod = process.getDeclaredMethod("myPid");

         if (!pidMethod.isAccessible()) {
            pidMethod.setAccessible(true);
         }
         return String.valueOf(pidMethod.invoke(null));
      } catch (Exception e) {
      }
      return null;
   }

   private static String getFromManagementFactory() { // JDK 6+
      try {
         Class type = Class.forName("java.lang.management.ManagementFactory");
         Method runtimeMXBeanMethod = type.getDeclaredMethod("getRuntimeMXBean");
         Method nameMethod = runtimeMXBeanMethod.getReturnType().getDeclaredMethod("getName");

         if (!runtimeMXBeanMethod.isAccessible()) {
            runtimeMXBeanMethod.setAccessible(true);
         }
         if (!nameMethod.isAccessible()) {
            nameMethod.setAccessible(true);
         }
         Object bean = runtimeMXBeanMethod.invoke(null);
         return String.valueOf(nameMethod.invoke(bean)).split("@")[0];
      } catch (Exception e) {
      }
      return null;
   }

   private static String getFromRuntimeMXBean() { // JDK 10+
      try {
         Class runtimeMXBean = Class.forName("java.lang.management.RuntimeMXBean");
         Method pidMethod = runtimeMXBean.getDeclaredMethod("getName");

         if (!pidMethod.isAccessible()) {
            pidMethod.setAccessible(true);
         }
         return String.valueOf(pidMethod.invoke(null));
      } catch (Exception e) {
      }
      return null;
   }
}
