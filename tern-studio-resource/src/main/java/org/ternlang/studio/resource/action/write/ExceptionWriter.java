package org.ternlang.studio.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public class ExceptionWriter<T extends Throwable> implements BodyWriter<T> {

   @Override
   public boolean accept(Context context, Object result) throws Exception {
      if (result != null) {
         return Throwable.class.isInstance(result);
      }
      return false;
   }

   @Override
   public void write(Context context, Throwable cause) throws Exception {
      Response response = context.getResponse();
      PrintStream output = response.getPrintStream();

      cause.printStackTrace(output);
   }

}
