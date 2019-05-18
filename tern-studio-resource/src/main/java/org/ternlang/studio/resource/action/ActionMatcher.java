package org.ternlang.studio.resource.action;

import java.util.Set;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourceMatcher;
import org.ternlang.studio.resource.action.validate.Validation;
import org.ternlang.studio.resource.action.write.ResponseWriter;

public class ActionMatcher implements ResourceMatcher {

   private final ActionResolver resolver;
   private final ContextBuilder builder;
   private final ResponseWriter router;

   public ActionMatcher(ActionResolver resolver, ContextBuilder builder, ResponseWriter router) {
      this.resolver = resolver;
      this.builder = builder;
      this.router = router;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Context context = builder.build(request, response);
      Action action = resolver.resolve(context);

      if (action != null) {
         Validation validation = context.getValidation();
         Set<String> errors = validation.getErrors();
         
         if(errors != null) {
            errors.clear();
         }
         return new ActionResource(router, action, context);
      }
      return null;
   }
}
