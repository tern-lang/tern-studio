package tern.studio.agent.log;

public interface Log {
   void log(LogLevel level, Object text);
   void log(LogLevel level, Object text, Throwable cause);
}