package org.ternlang.studio.resource.action.write;

import java.io.OutputStream;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public class ByteArrayWriter implements BodyWriter<byte[]> {

   @Override
   public boolean accept(Context context, Object result) throws Exception {
      if (result != null) {
         return byte[].class.isInstance(result);
      }
      return false;
   }

   @Override
   public void write(Context context, byte[] result) throws Exception {
      Response response = context.getResponse();
      OutputStream output = response.getOutputStream();
      
      response.setContentLength(result.length);
      output.write(result);
   }
}
