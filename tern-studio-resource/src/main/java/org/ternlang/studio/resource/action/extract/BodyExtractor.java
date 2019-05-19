package org.ternlang.studio.resource.action.extract;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.simpleframework.http.Request;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.Body;

public class BodyExtractor implements Extractor<Object> {

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      Body annotation = parameter.getAnnotation(Body.class);
      
      if(annotation != null) {
         Request request = context.getRequest();
         Class type = parameter.getType();
   
         if (type == InputStream.class) {
            return request.getInputStream();
         }
         if (type == ReadableByteChannel.class) {
            return request.getByteChannel();
         }
         if (type == String.class) {
            return request.getContent();
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Body annotation = parameter.getAnnotation(Body.class);
      
      if(annotation != null) {
         Class type = parameter.getType();
         
         if (type == InputStream.class) {
            return true;
         }
         if (type == ReadableByteChannel.class) {
            return true;
         }
         if (type == String.class) {
            return true;
         }
      }
      return false;
   }
}
