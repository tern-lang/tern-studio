package org.ternlang.studio.core;

import java.net.InetSocketAddress;

import org.simpleframework.module.annotation.Component;
import org.simpleframework.module.common.ComponentListener;
import org.simpleframework.module.resource.container.ResourceServer;
import org.ternlang.studio.common.ProgressManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StudioStartListener implements ComponentListener {
  
    private final StudioClientLauncher launcher;
    private final ProcessManager manager;
    private final ResourceServer starter;
    
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
}