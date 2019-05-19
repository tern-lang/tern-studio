package org.ternlang.studio.core.terminal;

public interface TerminalListener {
   void onTerminalInit();
   void onTerminalReady();
   void onTerminalCommand(String command);
   void onTerminalResize(String columns, String rows);
   void onTerminalClose();
}
