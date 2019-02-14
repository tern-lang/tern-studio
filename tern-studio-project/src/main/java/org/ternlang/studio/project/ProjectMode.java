package org.ternlang.studio.project;

public enum ProjectMode {
   DEBUG,
   DEVELOP,
   RUN;
   
   public boolean isSingleMode() {
      return this == DEBUG;
   }
   
   public boolean isMultipleMode() {
      return this == DEVELOP;
   }
   
   public static ProjectMode resolveMode(String token) {
      if(token != null) {
         ProjectMode[] modes = ProjectMode.values();
         
         for(ProjectMode mode : modes) {
            if(mode.name().equalsIgnoreCase(token)) {
               return mode;
            }
         }
      }
      return DEVELOP;
   }
}