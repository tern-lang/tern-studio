package tern.studio.agent.log;

public class LogLogger implements TraceLogger {
   
   private final LogLevel level;
   private final Log logger;

   public LogLogger(Log logger, LogLevel level) {
      this.logger = logger;
      this.level = level;
   }
   
   @Override
   public String getLevel() {
      return level.name();
   }
   
   @Override
   public boolean isTrace() {
      return level.isTraceEnabled();
   }
   
   @Override
   public boolean isDebug() {
      return level.isDebugEnabled();
   }
   
   @Override
   public void trace(String message) {
      if(level.isTraceEnabled()) {
         logger.log(LogLevel.TRACE, message);
      }
   }
   
   @Override
   public void trace(String message, Throwable cause) {
      if(level.isTraceEnabled()) {
         logger.log(LogLevel.TRACE, message, cause);
      }
   }
   
   @Override
   public void debug(String message) {
      if(level.isDebugEnabled()) {
         logger.log(LogLevel.DEBUG, message);
      }
   }
   
   @Override
   public void debug(String message, Throwable cause) {
      if(level.isDebugEnabled()) {
         logger.log(LogLevel.DEBUG, message, cause);
      }
   }
   
   @Override
   public void info(String message) {
      if(level.isInfoEnabled()) {
         logger.log(LogLevel.INFO, message);
      }
   }
   
   @Override
   public void info(String message, Throwable cause) {
      if(level.isInfoEnabled()) {
         logger.log(LogLevel.INFO, message, cause);
      }
   }
}