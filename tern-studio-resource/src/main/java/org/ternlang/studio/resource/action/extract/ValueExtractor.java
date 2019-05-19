package org.ternlang.studio.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.Value;

public class ValueExtractor extends StringConverterExtractor {

   public ValueExtractor() {
      super(Value.class);
   }
   
   @Override
   public List<String> extract(Parameter parameter, Request request, Response response) {
      Value annotation = parameter.getAnnotation(Value.class);
      
      if(annotation != null) {
         String name = annotation.value();
         int length = name.length();
         
         if(length > 0) {
            if(name.startsWith("${") && name.endsWith("}")) {
               String substitute = parameter.getDefault();
               String token = name.substring(2, length - 1);
               String value = System.getProperty(token, substitute);
               
               if(value != null) {
                  return Arrays.asList(value);
               }
            }
            return Arrays.asList(name);
         }
      }
      return null;
   }
}