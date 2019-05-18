package org.ternlang.studio.resource.action.extract;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.annotation.CookieParam;

public class CookieExtractor implements Extractor<Object> {

   private final StringConverter converter;

   public CookieExtractor() {
      this.converter = new StringConverter();
   }

   @Override
   public Object extract(Parameter parameter, Context context) {
      CookieParam annotation = parameter.getAnnotation(CookieParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Request request = context.getRequest();
         Class type = parameter.getType();
         String substitute = parameter.getDefault();
         Cookie cookie = request.getCookie(name);
   
         if (cookie != null) {
            String value = cookie.getValue();
            
            if (type == Cookie.class) {
               return cookie;
            }
            return converter.convert(type, value);
         }
         if (substitute != null) {
            return converter.convert(type, substitute);
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      CookieParam annotation = parameter.getAnnotation(CookieParam.class);
      
      if(annotation != null) {
         String name = annotation.value();
         Class type = parameter.getType();
   
         if (name != null) {
            if (type == Cookie.class) {
               return true;
            }
            return converter.accept(type);
         }
      }
      return false;
   }
}
