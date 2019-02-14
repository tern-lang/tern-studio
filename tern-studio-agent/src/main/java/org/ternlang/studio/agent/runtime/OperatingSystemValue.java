package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.OS;

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
