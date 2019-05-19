package org.ternlang.studio.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.HeaderParam;

public class HeaderExtractor extends StringConverterExtractor {

   public HeaderExtractor() {
      super(HeaderParam.class);
   }
   
   @Override
   public List<String> extract(Parameter parameter, Request request, Response response) {
      HeaderParam annotation = parameter.getAnnotation(HeaderParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         String substitute = parameter.getDefault();
         String value = request.getValue(name);
         
         if(value != null) {
            return Arrays.asList(value);
         }
         if(substitute != null) {
            return Arrays.asList(substitute);
         }
      }
      return null;
   }
}
