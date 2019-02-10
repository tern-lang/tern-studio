package tern.studio.build.console;

public interface ConsoleListener {
   void onUpdate(String process, String line);
   void onUpdate(String process, String line, Throwable cause);
}