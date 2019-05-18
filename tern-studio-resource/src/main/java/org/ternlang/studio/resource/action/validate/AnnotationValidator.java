package org.ternlang.studio.resource.action.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.ternlang.studio.resource.action.annotation.Required;

public class AnnotationValidator implements Validator {

   @Override
   public Set<String> validateObject(Object value) throws Exception {
      Set<String> errors = new HashSet<String>();

      if (value != null) {
         Class type = value.getClass();
         Field[] fields = type.getDeclaredFields();

         for (Field field : fields) {
            Set<String> violations = validateProperty(value, field);

            if (!violations.isEmpty()) {
               errors.addAll(violations);
            }
         }
      }
      return errors;
   }

   @Override
   public Set<String> validateProperty(Object value, Field field) throws Exception {
      Set<String> errors = new HashSet<String>();

      if (value != null) {
         Required required = field.getAnnotation(Required.class);

         if (!field.isAccessible()) {
            field.setAccessible(true);
         }
         Object object = field.get(value);

         if (object == null && required != null) {
            String message = required.value();

            if (message.isEmpty()) {
               errors.add("Required property " + field + " has not been set");
            } else {
               errors.add(message);
            }
         }
      }
      return errors;
   }

   @Override
   public Set<String> validateParameter(Method method, Object argument, int index) throws Exception {
      Set<String> errors = new HashSet<String>();

      if (argument == null) {
         Annotation[][] list = method.getParameterAnnotations();
         Required required = findAnnotation(Required.class, list[index]);

         if (required != null) {
            String message = required.value();

            if (message.isEmpty()) {
               errors.add("Required value missing for parameter " + index + " of " + method);
            } else {
               errors.add(message);
            }
         }
      }
      return errors;
   }

   @Override
   public Set<String> validateParameter(Constructor factory, Object argument, int index) throws Exception {
      Set<String> errors = new HashSet<String>();

      if (argument == null) {
         Annotation[][] list = factory.getParameterAnnotations();
         Required required = findAnnotation(Required.class, list[index]);

         if (required != null) {
            String message = required.value();

            if (message.isEmpty()) {
               errors.add("Required value missing for parameter " + index + " of " + factory);
            } else {
               errors.add(message);
            }
         }
      }
      return errors;
   }

   private <T extends Annotation> T findAnnotation(Class<T> type, Annotation[] annotations) throws Exception {
      for (Annotation annotation : annotations) {
         Class annotationType = annotation.annotationType();

         if (annotationType == type) {
            return (T) annotation;
         }
      }
      return null;
   }

}
