package tern.studio.common.server;

import java.util.Set;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleServer;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RestServer {
   
   private final ResourceClassScanner scanner;
   private final RestServerBuilder builder;
   private final int port;
   
   public RestServer(RestServerBuilder builder, @Value("${port}") int port) {
      this.scanner = new ResourceClassScanner();
      this.builder = builder;
      this.port = port;
   }

   public SimpleServer start(ApplicationContext context) {
      Set<Class<?>> resources = scanner.scan();
      
      try {
         ResourceConfig config = new ResourceConfig(resources);
         RestServiceLocatorFeature feature = new RestServiceLocatorFeature((ConfigurableApplicationContext)context);
 
         config.register(feature);
         
         return builder.create(config, null, port);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
   
   private static class RestServiceLocatorFeature implements Feature, Provider<ServiceLocator> {

      private ConfigurableApplicationContext spring;
      private ServiceLocator locator;

      public RestServiceLocatorFeature(ConfigurableApplicationContext spring) {
         this.spring = spring;
      }

      @Override
      public ServiceLocator get() {
         if(locator == null) {
            throw new IllegalStateException("Service locator is not yet available");
         }
         return locator;
      }

      @Override
      public boolean configure(FeatureContext context) {
         locator = ServiceLocatorProvider.getServiceLocator(context);

         SpringBridge.getSpringBridge().initializeSpringBridge(locator);
         SpringIntoHK2Bridge springBridge = locator.getService(SpringIntoHK2Bridge.class);
         springBridge.bridgeSpringBeanFactory(spring.getBeanFactory());

         return true;
      }
   }
}
