package org.ternlang.studio.resource.action.build;

import static org.simpleframework.http.Method.CONNECT;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.ternlang.studio.resource.action.Context;

public class PathResolver {

   public String resolve(Context context) throws Exception {
      Request request = context.getRequest();
      Path path = request.getPath();
      String normalized = path.getPath();
      String method = request.getMethod();
      
      if (method.equals(CONNECT)) { // connect uses domain:port rather than path
         return request.getTarget();
      }
      return normalized;
   }
}
