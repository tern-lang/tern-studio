package org.ternlang.studio.resource.action.extract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;

public abstract class StringConverterExtractor implements Extractor<Object> {

   private final Class<? extends Annotation> annotation;
   private final StringConverter converter;

   public StringConverterExtractor(Class<? extends Annotation> annotation) {
      this.converter = new StringConverter();
      this.annotation = annotation;
   }

   @Override
   public Object extract(Parameter parameter, Context context) {
      Request request = context.getRequest();
      Response response = context.getResponse();

      if (parameter != null) {
         List<String> values = extract(parameter, request, response);
         Class type = parameter.getType();

         if (values != null) {
            int size = values.size();
            
            if(type.isArray()) {
               Class entry = type.getComponentType();
               Object list = Array.newInstance(entry, size);
               
               for(int i = 0; i < size; i++) {
                  String value = values.get(i);
                  Object element = converter.convert(entry, value);
                  
                  if(element != null) {
                     Array.set(list, i, element);
                  }
               }
               return list;
            }           
            if(size > 0) {
               String value = values.get(0);
               Object result = converter.convert(type, value);
               
               if(result != null) {
                  return result;
               }
            }
         }
      }
      return null;
   }

   @Override
   public boolean accept(Parameter parameter) {
      Annotation value = parameter.getAnnotation(annotation);
      Class type = parameter.getType();

      if (value != null) {
         if(type.isArray()) {
            Class entry = type.getComponentType();
            
            if(entry != null) {
               return converter.accept(entry); 
            }
            return false;
         }
         return converter.accept(type);
      }
      return false;
   }

   protected abstract List<String> extract(Parameter parameter, Request request, Response response);
}
