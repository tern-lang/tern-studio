package tern.studio.common.log;

import org.slf4j.Logger;
import tern.studio.agent.log.Log;
import tern.studio.agent.log.LogLevel;

public class LoggerLog implements Log {
   
   private final Logger log;
   
   public LoggerLog(Logger log) {
      this.log = log;
   }

   @Override
   public void log(LogLevel level, Object text) {
      String message = String.valueOf(text);
      
      if(level == LogLevel.TRACE) {
         log.trace(message);
      } else if(level == LogLevel.DEBUG) {
         log.debug(message);
      } else if(level == LogLevel.INFO) {
         log.info(message);
      } else {
         log.error(message);
      }
   }

   @Override
   public void log(LogLevel level, Object text, Throwable cause) {
      String message = String.valueOf(text);
      
      if(level == LogLevel.TRACE) {
         log.trace(message, cause);
      } else if(level == LogLevel.DEBUG) {
         log.debug(message);
      } else if(level == LogLevel.INFO) {
         log.info(message);
      } else {
         log.error(message);
      }
   }
   
}