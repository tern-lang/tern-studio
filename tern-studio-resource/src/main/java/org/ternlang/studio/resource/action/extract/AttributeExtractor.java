package org.ternlang.studio.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.AttributeParam;

public class AttributeExtractor extends StringConverterExtractor {
   
   public AttributeExtractor() {
      super(AttributeParam.class);
   }

   @Override
   public List<String> extract(Parameter parameter, Request request, Response response) {
      AttributeParam annotation = parameter.getAnnotation(AttributeParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         String substitute = parameter.getDefault();
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
         if (substitute != null) {
            return Arrays.asList(substitute);
         }
      }
      return null;
   }
}
