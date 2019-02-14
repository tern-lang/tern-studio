package org.ternlang.studio.agent.runtime;

public enum RuntimeAttribute {
   VERSION("version"),
   SCRIPT("script"),
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
