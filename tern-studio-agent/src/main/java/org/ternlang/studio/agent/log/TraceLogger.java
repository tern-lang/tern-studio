package org.ternlang.studio.agent.log;

public interface TraceLogger {
   String getLevel();
   boolean isTrace();
   boolean isDebug();
   void trace(String message);
   void trace(String message, Throwable cause);
   void debug(String message);
   void debug(String message, Throwable cause);
   void info(String message);
   void info(String message, Throwable cause);
}