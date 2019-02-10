package tern.studio.common.resource;

import static org.simpleframework.http.Method.CONNECT;

import java.util.List;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import tern.common.LeastRecentlyUsedMap;
import org.springframework.stereotype.Component;

@Component
public class RegularExpressionMatcher implements ResourceMatcher {

   private final Map<String, Resource> cache;
   private final ResourceSet resources;
   
   public RegularExpressionMatcher(ResourceSet resources) {
      this.cache = new LeastRecentlyUsedMap<String, Resource>(1000);
      this.resources = resources;
   }

   @Override
   public synchronized Resource match(Request request, Response response) {
      Path path = request.getPath();
      String target = path.getPath();
      String method = request.getMethod();

      if (method.equals(CONNECT)) { // connect uses domain:port rather than path
         target = request.getTarget();
      }
      Resource resource = cache.get(target);

      if (resource == null) {
         resource = match(request, target);

         if (resource != null) {
            cache.put(target, resource);
         }
      }
      return resource;
   }

   private synchronized Resource match(Request request, String target) {
      List<Resource> list = resources.getResources();

      for (Resource resource : list) {
         Class<?> type = resource.getClass();
         ResourcePath path = type.getAnnotation(ResourcePath.class);

         if (path == null) {
            throw new IllegalStateException("Could not find annotation on " + type);
         }
         String expression = path.value();

         if (target.matches(expression)) {
            return resource;
         }
      }
      return null;
   }
}