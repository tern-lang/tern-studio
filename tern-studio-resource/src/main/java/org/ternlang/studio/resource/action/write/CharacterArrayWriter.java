package org.ternlang.studio.resource.action.write;

import java.io.PrintStream;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public class CharacterArrayWriter implements BodyWriter<char[]> {

   @Override
   public boolean accept(Context context, Object result) throws Exception {
      if (result != null) {
         return char[].class.isInstance(result);
      }
      return false;
   }

   @Override
   public void write(Context context, char[] result) throws Exception {
      Response response = context.getResponse();
      PrintStream output = response.getPrintStream();

      output.print(result);
   }

}
