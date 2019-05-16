package org.ternlang.studio.common.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainer;
import org.glassfish.jersey.simple.SimpleServer;
import org.glassfish.jersey.simple.SimpleTraceAnalyzer;
import org.glassfish.jersey.simple.internal.LocalizationMessages;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.http.socket.service.Router;
import org.simpleframework.http.socket.service.RouterContainer;
import org.simpleframework.http.socket.service.Service;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.common.resource.CombinationMatcher;
import org.ternlang.studio.common.resource.ResourcePathRouter;

@Component
public class RestServerBuilder {
   
   private final CombinationMatcher matcher;
   private final Router router;

   public RestServerBuilder(CombinationMatcher matcher, Optional<List<Service>> services) {
      this.router = new ResourcePathRouter(services);
      this.matcher = matcher;
   }
   
   public SimpleServer create(ResourceConfig config, SSLContext context, int port) throws Exception {
      SimpleContainer container = create(config);
      Container inner = create(container);
      
      try {
         SimpleTraceAnalyzer analyzer = new SimpleTraceAnalyzer();
         SocketProcessor server = new ContainerSocketProcessor(inner);
         Connection connection = new SocketConnection(server, analyzer);
         InetSocketAddress listen = new InetSocketAddress(port);
         InetSocketAddress bound = (InetSocketAddress)connection.connect(listen, context);
         int bindPort = bound.getPort();
         
         container.getApplicationHandler().onStartup(container);
         ProgressManager.getProgress().update("Container started on " + bindPort);

         return new InternalServer(container, analyzer, connection, bindPort);
      } catch (final IOException ex) {
         throw new ProcessingException(LocalizationMessages.ERROR_WHEN_CREATING_SERVER(), ex);
      }
   }
   
   private Container create(SimpleContainer container) {
      try {
         RestServerContainer delegate = new RestServerContainer(container, matcher);
   
         if(router != null) {
            return new RouterContainer(delegate, router, 5);
         }
         return delegate;
      } catch(Exception e) {
         throw new IllegalStateException("Could not create router container", e);
      }
   }
   
   private SimpleContainer create(ResourceConfig config) {
      try {
         Constructor<SimpleContainer> constructor = SimpleContainer.class.getDeclaredConstructor(Application.class);
         constructor.setAccessible(true);
         return constructor.newInstance(config);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
   
   private static class InternalServer implements SimpleServer {
      
      private final SimpleTraceAnalyzer analyzer;
      private final SimpleContainer container;
      private final Connection connection;
      private final int port;
      
      public InternalServer(SimpleContainer container, SimpleTraceAnalyzer analyzer, Connection connection, int port) {
         this.analyzer = analyzer;
         this.container = container;
         this.connection = connection;
         this.port = port;
      }
      
      @Override
      public void close() throws IOException {
         container.getApplicationHandler().onShutdown(container);
         analyzer.stop();
         connection.close();
      }

      @Override
      public int getPort() {
         return port;
      }

      @Override
      public boolean isDebug() {
         return analyzer.isActive();
      }

      @Override
      public void setDebug(boolean enable) {
         if (enable) {
            analyzer.start();
         } else {
            analyzer.stop();
         }
      }
      
   }
}
