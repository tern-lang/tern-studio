package org.ternlang.studio.agent.log;

import java.io.PrintStream;

public class ConsoleLog implements Log{
   
   private final PrintStream stream;
   
   public ConsoleLog() {
      this.stream = System.out;
   }

   @Override
   public void log(LogLevel level, Object text) {
      stream.println(text);
   }

   @Override
   public void log(LogLevel level, Object text, Throwable cause) {
      stream.print(text);
      
      if(cause != null) {
         stream.print(": ");
         cause.printStackTrace(stream);
      }else {
         stream.println();
      }
   }

}