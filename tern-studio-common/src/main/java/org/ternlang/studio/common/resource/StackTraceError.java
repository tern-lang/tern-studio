package org.ternlang.studio.common.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceError {

   private Throwable cause;

   public StackTraceError() {
      super();
   }

   public String getMessage() {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);

      if (cause != null) {
         cause.printStackTrace(writer);
      }
      return buffer.toString();
   }

   public void setCause(Throwable cause) {
      this.cause = cause;
   }

   public Throwable getCause() {
      return cause;
   }

   @Override
   public String toString() {
      return getMessage();
   }
}