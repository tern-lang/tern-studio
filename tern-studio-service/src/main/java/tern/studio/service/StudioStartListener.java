package tern.studio.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.simple.SimpleServer;
import tern.studio.common.ProgressManager;
import tern.studio.common.server.RestServer;
import tern.ui.ClientEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class StudioStartListener implements ApplicationListener<ContextRefreshedEvent> {
  
    private final StudioClientLauncher launcher;
    private final ProcessManager manager;
    private final RestServer starter;
   
    public void onApplicationEvent(ContextRefreshedEvent event) {
       try {
          ApplicationContext context = event.getApplicationContext();
          SimpleServer server = starter.start(context);
          int port = server.getPort();
          String host = "localhost"; //InetAddress.getLocalHost().getHostName();
          String project = String.format("http://%s:%s/", host, port);
          String script = StudioOption.SCRIPT.getValue();
          String browser = StudioOption.BROWSER_ENGINE.getValue();
          
          ProgressManager.getProgress().update("Starting service on " + port);
          log.info("Listening to " + project);
             
          if(script != null) {
             manager.launch(); // start a new process
          }
          ClientEngine engine = ClientEngine.resolveEngine(browser);
          launcher.launch(engine, host, port);
          manager.start(host, port);
          ProgressManager.getProgress().update("Service started at " + port);
       } catch(Exception e) {
          throw new IllegalStateException("Could not start server", e);
       }
    }
}