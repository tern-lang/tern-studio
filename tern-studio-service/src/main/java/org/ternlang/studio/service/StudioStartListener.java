package org.ternlang.studio.service;

import java.net.InetSocketAddress;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.resource.action.annotation.ComponentListener;
import org.ternlang.studio.resource.server.RestServer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.ternlang.studio.resource.action.annotation.Component
@Component
@AllArgsConstructor
public class StudioStartListener implements ApplicationListener<ContextRefreshedEvent>, ComponentListener {
  
    private final StudioClientLauncher launcher;
    private final ProcessManager manager;
    private final RestServer starter;
    
    @Override
    public void onReady() {
       try {
          InetSocketAddress address = starter.start();
          int port = address.getPort();
          String host = "localhost"; //InetAddress.getLocalHost().getHostName();
          String project = String.format("http://%s:%s/", host, port);
          String script = StudioOption.SCRIPT.getValue();
          
          ProgressManager.getProgress().update("Starting service on " + port);
          log.info("Listening to " + project);
             
          if(script != null) {
             manager.launch(); // start a new process
          }
          launcher.launch(host, port);
          manager.start(host, port);
          ProgressManager.getProgress().update("Service started at " + port);
       } catch(Exception e) {
          throw new IllegalStateException("Could not start server", e);
       }
    }
   
    public void onApplicationEvent(ContextRefreshedEvent event) {
       onReady();
    }
}