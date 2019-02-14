package org.ternlang.studio.agent.core;

public enum ExecuteStatus {
   REGISTERING(false, false, false, 0),
   WAITING(false, false, false, 1),
   STARTING(false, false, true, 2),
   COMPILING(false, false, true, 3),
   DEBUGGING(true, true, true, 4),
   RUNNING(true, false, true, 4),
   TERMINATING(true, false, true, 5),
   FINISHED(false, false, true, 6);   
   
   private final boolean running;
   private final boolean debug;
   private final boolean started; // is a resource required
   private final int phase;
   
   private ExecuteStatus(boolean running, boolean debug, boolean started, int phase) {
      this.started = started;
      this.running = running;
      this.debug = debug;
      this.phase = phase;
   }
   
   public boolean isFinished() {
      return this == FINISHED;
   }
   
   public boolean isStarted(){
      return started; // must have a resource
   }

   public boolean isRunning() {
      return running;
   }
   
   public boolean isDebug() {
      return debug;
   }
   
   public boolean isTransitionForward(ExecuteStatus next) {
      return next.phase > phase;
   }
   
   public boolean isTransitionPossible(ExecuteStatus next) {
      return next.phase >= phase;
   }
   
   public static ExecuteStatus resolveStatus(String token) {
      if(token != null) {
         ExecuteStatus[] statuses = ExecuteStatus.values();
         
         for(ExecuteStatus mode : statuses) {
            String name = mode.name();
            
            if(name.equalsIgnoreCase(token)) {
               return mode;
            }
         }
      }
      return WAITING;
   }
}
