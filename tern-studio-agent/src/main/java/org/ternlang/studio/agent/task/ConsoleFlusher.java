package org.ternlang.studio.agent.task;

public class ConsoleFlusher {

   public static void flush() {
      System.err.flush(); // flush output to sockets
      System.out.flush();
   }
   
   public static void flushError(Throwable cause) {
      String text = ExceptionBuilder.build(cause);
      System.err.println(text);
      System.err.flush(); // flush output to sockets
   }
}
