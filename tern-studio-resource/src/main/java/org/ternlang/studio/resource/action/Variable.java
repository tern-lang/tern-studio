package org.ternlang.studio.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public enum Variable {
   REQUEST("request") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request;
      }
   },
   RESPONSE("response") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return response;
      }
   },
   PARAMETERS("parameters") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getQuery();
      }
   },
   ATTRIBUTES("attributes") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getAttributes();
      }
   },
   MESSAGE("message") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return request.getContent();
      }
   },
   VALIDATION("validation") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return context.getValidation();
      }
   },
   ERROR("error") {
      public Object extract(Request request, Response response, Context context) throws Exception {
         return context.getError();
      }
   };

   private final String name;

   private Variable(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void update(Request request, Response response, Context context) throws Exception {
      Object value = extract(request, response, context);
      Model model = context.getModel();

      if (value != null) {
         model.setAttribute(name, value);
      }
   }

   public abstract Object extract(Request request, Response response, Context context) throws Exception;
}
