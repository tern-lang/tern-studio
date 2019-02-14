package org.ternlang.studio.common.server;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Protocol.DATE;
import static org.simpleframework.http.Protocol.SERVER;
import static org.ternlang.studio.common.resource.SessionConstants.SESSION_ID;

import java.io.IOException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.simple.SimpleContainer;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.ternlang.studio.common.resource.CombinationMatcher;
import org.ternlang.studio.common.resource.Resource;

@Slf4j
public class RestServerContainer implements Container {
   
   private static final String SERVER_NAME = "Apache/2.2.14";

   private final CombinationMatcher matcher;
   private final SimpleContainer container;

   public RestServerContainer(SimpleContainer container, CombinationMatcher matcher) {
      this.matcher = matcher;
      this.container = container;
   }

   @Override
   public void handle(Request request, Response response) {
      long time = System.currentTimeMillis();
      String method = request.getMethod();
      
      try {
         Resource resource = matcher.match(request, response);
         Cookie cookie = request.getCookie(SESSION_ID);
         
         if(cookie == null) {
            String value = UUID.randomUUID().toString();
            response.setCookie(SESSION_ID, value);
         }
         response.setDate(DATE, time);
         response.setValue(SERVER, SERVER_NAME);
         response.setDate(DATE, time);
         
         if(resource != null) {
            response.setStatus(Status.OK);
            resource.handle(request, response);
         } else {
            container.handle(request, response);
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