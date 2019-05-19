package org.ternlang.studio.resource.action.build;

import static java.lang.Integer.MIN_VALUE;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ternlang.core.Bug;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.extract.ParameterBuilder;
import org.ternlang.studio.resource.action.validate.Validation;
import org.ternlang.studio.resource.action.validate.Validator;

public class MethodExecutor {

   private static final Logger LOG = LoggerFactory.getLogger(MethodExecutor.class);

   private final ParameterBuilder extractor;
   private final PathResolver resolver;
   private final MethodMatcher matcher;
   private final MethodHeader header;
   private final Validator validator;
   private final Method method;

   public MethodExecutor(MethodMatcher matcher, MethodHeader header, ParameterBuilder extractor, Validator validator, Method method) {
      this.resolver = new PathResolver();
      this.extractor = extractor;
      this.validator = validator;
      this.matcher = matcher;
      this.header = header;
      this.method = method;
   }

   public Object execute(Object value, Context context) throws Exception {
      try {
         evaluate(value, context);

         if (valid(context)) {
            return invoke(value, context);               
         }
      } catch (Throwable cause) {
         context.setError(cause);
         report(context, cause);
         return cause;
      }
      return null;
   }

   private void report(Context context, Throwable cause) {
      Request request = context.getRequest();
      Response response = context.getResponse();

      LOG.info("Error method " + method + " from " + request + response, cause);
   }

   public float score(Context context) throws Exception {
      Request request = context.getRequest();
      String method = request.getMethod();
      String verb = matcher.verb();
      
      if(method.equalsIgnoreCase(verb)) {
         Path path = request.getPath();
         String normal = path.getPath();
         String ignore = matcher.ignore();
         
         if(ignore.isEmpty() || !normal.matches(ignore)) {  
            return extractor.score(context);
         }
      }
      return MIN_VALUE;
   }

   @Bug("what happens if uncommitted???")
   private Object invoke(Object value, Context context) throws Exception {
      Object[] arguments = extractor.extract(context);
      Object result = method.invoke(value, arguments);

      if (result == null) {
         Response response = context.getResponse();

         if (!response.isCommitted()) {
            //throw new RuntimeException("Should have been committed here");
         }
      }
      return result;
   }

   private void evaluate(Object value, Context context) throws Exception {
      Response response = context.getResponse();
      Request request = context.getRequest();
      String normalized = resolver.resolve(context);
      Map<String, String> parameters = matcher.evaluate(normalized);
      Map attributes = request.getAttributes();

      if (!parameters.isEmpty()) {
         attributes.putAll(parameters);
      }
      if (!response.isCommitted()) {
         header.applyHeader(context);
      }
   }

   private boolean valid(Context context) throws Exception {
      Object[] arguments = extractor.extract(context);
      Validation validation = context.getValidation();

      for (int i = 0; i < arguments.length; i++) {
         Set<String> violations = validator.validateParameter(method, arguments[i], i);

         for (String violation : violations) {
            validation.addError(violation);
         }
      }
      return validation.isValid();
   }

   @Override
   public String toString() {
      return method.toString();
   }
}
