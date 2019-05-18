package org.ternlang.studio.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public class StringWriter implements BodyWriter<String> {

   @Override
   public boolean accept(Context context, Object result) throws Exception {
      if (result != null) {
         return String.class.isInstance(result);
      }
      return false;
   }

   @Override
   public void write(Context context, String result) throws Exception {
      Response response = context.getResponse();
      PrintStream output = response.getPrintStream();

      output.print(result);
   }
}
