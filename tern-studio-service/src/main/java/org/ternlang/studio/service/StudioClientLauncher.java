package org.ternlang.studio.service;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ternlang.studio.common.ProgressManager;
import org.ternlang.studio.project.HomeDirectory;
import org.ternlang.ui.ClientContext;
import org.ternlang.ui.ClientControl;
import org.ternlang.ui.ClientProvider;

@Slf4j
@Component
public class StudioClientLauncher {
   
   public static final String CLIENT_LOG = "client.log";
   public static final String CLIENT_CACHE = "cache";         

   private final AtomicReference<ClientControl> reference;
   private final File directory;
   private final boolean disabled;
   private final boolean debug;
   
   public StudioClientLauncher(
         @Value("${directory}") File directory, 
         @Value("${server-only}") boolean disabled, 
         @Value("${client-debug}") boolean debug)
   {
      this.reference = new AtomicReference<ClientControl>();
      this.directory = directory;
      this.disabled = disabled;
      this.debug = debug;
   }

   @SneakyThrows
   public void launch(final String host, final int port) {
      if (!disabled) {
         if(port != -1 && port != 80 && port != 443 && port != 0) {
            launch(String.format("http://%s:%s", host, port));
         } else {
            if(port == 443) {
               launch(String.format("https://%s", host));
            } else {
               launch(String.format("http://%s", host));
            }
         }
      }
   }

   @SneakyThrows
   public void launch(final String address) {
      if (!disabled) {
         final File root = HomeDirectory.getRootPath();
         final String path = root.getCanonicalPath();
         final File logFile = HomeDirectory.getPath(CLIENT_LOG);
         final File cachePath = HomeDirectory.getPath(CLIENT_CACHE);
         final String title = directory.getCanonicalPath();
         final ClientContext context = new ClientContext()
            .setLogFile(logFile)
            .setCachePath(cachePath)
            .setFolder(path)
            .setDebug(debug)            
            .setAddress(address)
            .setTitle(title);
         
         ProgressManager.getProgress().update("Creating client");
         context.validate();
         
         final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                  ClientControl control = ClientProvider.provide().create(context);
                  SplashPanel panel = SplashScreen.getPanel();
                  
                  panel.dispose();

                  if(reference.compareAndSet(null, control)) {
                     control.show();
                     control.closeOnExit(true);
                  } else {
                     ClientControl previous = reference.get();
                     ClientContext original = previous.getContext();
                     String root = original.getAddress();

                     if(address.startsWith(root)) { // do not allow random windows
                        log.info("Opening '{}'", address);
                        control.show();
                        control.closeOnExit(false);
                     } else {
                        log.info("Could not open '{}' it is not a child of '{}'", address, root);
                        control.dispose();
                     }
                  }
               } catch(Exception e) {
                  log.info("Could not show client", e);
               }
            }
         });
         thread.start();
      }
   }

   public void debug() {
      ClientControl control = reference.get();

      if(control != null) {
         control.showDebugger();
      }
   }

}