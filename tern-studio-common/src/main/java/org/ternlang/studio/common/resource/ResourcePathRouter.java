package org.ternlang.studio.common.resource;

import static org.simpleframework.http.Protocol.UPGRADE;
import static org.simpleframework.http.Protocol.WEBSOCKET;

import java.util.List;
import java.util.Optional;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.socket.service.DirectRouter;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.Service;
import org.ternlang.common.Cache;
import org.ternlang.common.LeastRecentlyUsedCache;

public class ResourcePathRouter implements Router {
   
   private final Optional<List<Service>> services;
   private final Cache<String, Router> routes;

   public ResourcePathRouter(Optional<List<Service>> services) {
      this(services, 100);
   }
   
   public ResourcePathRouter(Optional<List<Service>> services, int capacity) {
      this.routes = new LeastRecentlyUsedCache<String, Router>(capacity);
      this.services = services;
   }

   @Override
   public Service route(Request request, Response response) {
      String token = request.getValue(UPGRADE);
      
      if(token != null) {
         if(token.equalsIgnoreCase(WEBSOCKET)) {
            Path path = request.getPath();
            String normal = path.getPath();
            Router router = routes.fetch(normal);
            
            if(router == null) {
               if(services.isPresent()) {
                  List<Service> list = services.get();
                  
                  for(Service service : list) {
                     Class<?> type = service.getClass();
                     ResourcePath label = type.getAnnotation(ResourcePath.class);
                     
                     if(label != null) {
                        String pattern = label.value();
                        
                        if(normal.matches(pattern)) {
                           router = new DirectRouter(service);
                           routes.cache(normal, router);
                           return router.route(request, response);
                        }
                     }
                  }
               }
               return null;
            }
            return router.route(request, response);
         }
      }
      return null;
   }



}
