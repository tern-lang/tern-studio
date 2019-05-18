package org.ternlang.studio.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.PathParam;

public class PathExtractor extends StringConverterExtractor {

   public PathExtractor() {
      super(PathParam.class);
   }
   
   @Override
   public List<String> extract(Parameter parameter, Request request, Response response) {
      PathParam annotation = parameter.getAnnotation(PathParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Object value = request.getAttribute(name);
         
         if (value != null) {
            Class actual = value.getClass();
   
            if (actual == String[].class) {
               return Arrays.asList((String[])value);
            }
            if (actual == String.class) {
               return Arrays.asList((String)value);
            }
         }
      }
      return null;
   }
}
