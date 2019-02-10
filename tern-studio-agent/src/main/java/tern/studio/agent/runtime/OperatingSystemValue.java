package tern.studio.agent.runtime;

import static tern.studio.agent.runtime.RuntimeAttribute.OS;

public class OperatingSystemValue implements RuntimeValue {

   @Override
   public String getName() {
      return OS.name;
   }

   @Override
   public String getValue() {
      return System.getProperty("os.name");
   }
}
