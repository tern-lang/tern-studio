package org.ternlang.studio.resource.action.extract;

import org.simpleframework.http.Part;
import org.simpleframework.http.Request;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.PartParam;

public class PartExtractor implements Extractor<Object> {

   @Override
   public Object extract(Parameter parameter, Context context) throws Exception {
      PartParam annotation = parameter.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         String substitute = parameter.getDefault();
         Request request = context.getRequest();
         Class type = parameter.getType();
   
         if (type == Part.class) {
            return request.getPart(name);
         }
         if (type == String.class) {
            Part part = request.getPart(name);
   
            if (part != null) {
               return part.getContent();
            }
            return substitute;
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) throws Exception {
      PartParam annotation = parameter.getAnnotation(PartParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Class type = parameter.getType();
   
         if (type == Part.class) {
            return name != null;
         }
      }
      return false;
   }
}
