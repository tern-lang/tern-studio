package org.ternlang.studio.agent.runtime;

public enum RuntimeAttribute {
   MAIN_CLASS("main-class"),
   MAIN_SCRIPT("main-script"),
   VERSION("version"),
   PID("pid"),
   HOST("host"),
   USER("user"),
   OS("os");

   public final String name;

   private RuntimeAttribute(String name) {
      this.name = name;
   }

   public String getValue() {
      return RuntimeState.getValue(name);
   }
}
