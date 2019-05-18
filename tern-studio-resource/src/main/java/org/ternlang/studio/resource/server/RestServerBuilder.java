package org.ternlang.studio.resource.server;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.springframework.stereotype.Component;
import org.ternlang.studio.resource.ResourceMatcher;
import org.ternlang.studio.resource.ResourcePathRouter;
import org.ternlang.studio.resource.ResourceSystem;

@Component
public class RestServerBuilder {
   
   private final ResourceMatcher matcher;
   private final Router router;

   public RestServerBuilder(ResourceSystem system, ResourcePathRouter router) {
      this.matcher = system.create();
      this.router = router;
   }
   
   public InetSocketAddress create(SSLContext context, int port) throws Exception {
      RestServerContainer container = new RestServerContainer(matcher);
      RouterContainer wrapper = new RouterContainer(container, router, 5);
      SocketProcessor server = new ContainerSocketProcessor(wrapper);
      Connection connection = new SocketConnection(server);
      InetSocketAddress listen = new InetSocketAddress(port);
      
      return (InetSocketAddress)connection.connect(listen, context);
   }
}
