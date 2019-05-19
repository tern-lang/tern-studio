package org.ternlang.studio.resource;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;

import java.io.IOException;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.ternlang.studio.resource.action.annotation.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResourceContainer implements Container {

   private final ResourceMatcher matcher;

   public ResourceContainer(ResourceSystem system) {
      this.matcher = system.create();
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      
      try {
         Resource resource = matcher.match(request, response);

         response.setDate(DATE, time);
         
         if(resource != null) {
            response.setStatus(Status.OK);
            resource.handle(request, response);
         } else {
            response.setStatus(Status.NOT_FOUND);
            response.close();
         }
      } catch (Throwable cause) {
         log.info("Error handling resource", cause);
      } finally {
         try {
            if(!method.equals(CONNECT)) {
               response.close();
            }
         } catch (IOException ignore) {
            log.info("Could not close response", ignore);
         }
      }
   }
}