package org.ternlang.studio.service.terminal;

public interface TerminalListener {
   void onTerminalInit();
   void onTerminalReady();
   void onTerminalCommand(String command);
   void onTerminalResize(String columns, String rows);
   void onTerminalClose();
}
