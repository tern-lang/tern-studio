package org.ternlang.studio.resource.action.extract;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.WritableByteChannel;

import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public class ResponseExtractor implements Extractor<Object> {

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      Response response = context.getResponse();
      Class type = parameter.getType();

      if (type == Response.class) {
         return response;
      }
      if (type == OutputStream.class) {
         return response.getOutputStream();
      }
      if (type == PrintStream.class) {
         return response.getOutputStream();
      }
      if (type == WritableByteChannel.class) {
         return response.getByteChannel();
      }
      return response;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();

      if (type == Response.class) {
         return true;
      }
      if (type == OutputStream.class) {
         return true;
      }
      if (type == PrintStream.class) {
         return true;
      }
      if (type == WritableByteChannel.class) {
         return true;
      }
      return false;
   }
}
