package org.ternlang.studio.resource.action.build;

public class MethodPath {

   private final String ignore;
   private final String match;
   
   public MethodPath(String ignore, String match) {
      this.ignore = ignore;
      this.match = match;
   }
   
   public String getIgnore() {
      return ignore;
   }
   
   public String getMatch() {
      return match;
   }
}
