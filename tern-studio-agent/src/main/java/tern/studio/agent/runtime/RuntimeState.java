package tern.studio.agent.runtime;

public class RuntimeState {

   private static final RuntimeValueSource INSTANCE = new RuntimeValueSource(
           HostAddressValue.class,
           ProcessIdentityValue.class,
           ProcessUserValue.class,
           OperatingSystemValue.class,
           MainScriptValue.class,
           VersionValue.class
   );

   public static String getValue(String name) {
      return INSTANCE.getAttribute(name);
   }
}
