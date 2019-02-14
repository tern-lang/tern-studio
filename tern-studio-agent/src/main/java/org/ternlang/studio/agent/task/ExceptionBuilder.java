package org.ternlang.studio.agent.task;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionBuilder {

   public static String build(Throwable cause) {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);
      
      cause.printStackTrace(writer);
      writer.flush();
      writer.close();
      
      return buffer.toString();
   }
}