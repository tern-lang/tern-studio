package org.ternlang.studio.resource.action.extract;

import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.annotation.QueryParam;

public class QueryExtractor extends StringConverterExtractor {

   public QueryExtractor() {
      super(QueryParam.class);
   }
   
   @Override
   public List<String> extract(Parameter parameter, Request request, Response response) {
      QueryParam annotation = parameter.getAnnotation(QueryParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Query query = request.getQuery();    
         String substitute = parameter.getDefault();
         List<String> list = query.getAll(name);
         
         if(list != null) {
            if(!list.isEmpty()) {
               return list;
            }
         }
         if(substitute != null) {
            return Arrays.asList(substitute);
         }
      }
      return null;
   }
}
