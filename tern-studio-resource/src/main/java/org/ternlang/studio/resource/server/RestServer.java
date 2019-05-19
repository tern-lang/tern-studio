package org.ternlang.studio.resource.server;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@org.ternlang.studio.resource.action.annotation.Component
@Component
public class RestServer {
   
   private final RestServerBuilder builder;
   private final int port;
   
   public RestServer(RestServerBuilder builder, @org.ternlang.studio.resource.action.annotation.Value("${port}") @Value("${port}") int port) {
      this.builder = builder;
      this.port = port;
   }

   public InetSocketAddress start() {
      try {
         return builder.create(null, port);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
}
