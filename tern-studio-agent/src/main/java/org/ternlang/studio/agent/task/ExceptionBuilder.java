package org.ternlang.studio.agent.task;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.ternlang.core.error.InternalException;

public class ExceptionBuilder {

   public static String buildAll(Throwable cause) {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);
      
      if(cause != null) {
         cause.printStackTrace(writer);
      }
      writer.flush();
      writer.close();
      
      return buffer.toString();
   }
   
   public static String buildRoot(Throwable cause) {
      Throwable original = cause;
      
      while(cause != null) {
         Throwable internal = cause.getCause();
         
         if(internal == null) {
            break;
         }
         if(!InternalException.class.isInstance(internal)) {
            return buildAll(internal);
         }
      }
      return buildAll(cause == null ? original : cause);
   }
}