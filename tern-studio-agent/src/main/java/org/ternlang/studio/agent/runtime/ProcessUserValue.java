package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.USER;

public class ProcessUserValue implements RuntimeValue {

   @Override
   public String getName() {
      return USER.name;
   }

   @Override
   public String getValue() {
      return System.getProperty("user.name");
   }
}
