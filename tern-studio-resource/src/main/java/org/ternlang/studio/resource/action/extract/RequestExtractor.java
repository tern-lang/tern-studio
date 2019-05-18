package org.ternlang.studio.resource.action.extract;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.Inject;

public class RequestExtractor implements Extractor<Object> {

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      Request request = context.getRequest();
      Class type = parameter.getType();

      if (type == Request.class) {
         return request;
      }
      if (type == Query.class) {
         return request.getQuery();
      }
      if (type == InputStream.class) {
         return request.getInputStream();
      }
      if (type == ReadableByteChannel.class) {
         return request.getByteChannel();
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Class type = parameter.getType();
      
      if (type == Request.class) {
         return true;
      }
      if (type == Query.class) {
         return true;
      }
      if (type == InputStream.class) {
         return true;
      }
      if (type == ReadableByteChannel.class) {
         return true;
      }
      return false;
   }
}
