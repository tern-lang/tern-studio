package org.ternlang.studio.resource.action.build;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.action.Action;
import org.ternlang.studio.resource.action.Context;

public class ResourceAction implements Action {
   
   private final Resource resource;
   
   public ResourceAction(Resource resource) {
      this.resource = resource;
   }

   @Override
   public Object execute(Context context) throws Throwable {
      Request request = context.getRequest();
      Response response = context.getResponse();
      
      if(resource != null) {
         resource.handle(request, response);
      }
      return null;
   }
}
