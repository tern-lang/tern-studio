package org.ternlang.studio.agent.log;

public enum LogLevel {
   TRACE(0),
   DEBUG(1),
   INFO(2);
   
   private final int level;
   
   private LogLevel(int level){
      this.level = level;
   }
   
   public boolean isLevelEnabled(LogLevel level) {
      return this.level <= level.level;
   }
   
   public boolean isTraceEnabled(){
      return level <= TRACE.level;
   }
   
   public boolean isDebugEnabled(){
      return level <= DEBUG.level;
   }
   
   public boolean isInfoEnabled(){
      return level <= INFO.level;
   }
   
   public static LogLevel resolveLevel(String token){
      if(token != null) {
         String match = token.trim();
         LogLevel[] levels = values();
         
         for(LogLevel level : levels){
            String name = level.name();
            
            if(name.equalsIgnoreCase(match)) {
               return level;
            }
         }
      }
      return LogLevel.INFO;
      
   }
}
