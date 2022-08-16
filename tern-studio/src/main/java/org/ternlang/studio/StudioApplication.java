package org.ternlang.studio;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.http.ContentType;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.module.Application;
import org.simpleframework.module.annotation.Import;
import org.simpleframework.module.annotation.Module;
import org.simpleframework.resource.container.ServerDriver;
import org.ternlang.common.thread.ThreadBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.ternlang.studio.common.SessionCookie.SESSION_ID;

@Module
@Import(StudioApplication.class)
public class StudioApplication {

   public static void main(String[] list) throws Exception {
      StudioServiceBuilder builder = new StudioServiceBuilder(list);
      StudioAccessLogger logger = new StudioAccessLogger();
      StudioService service = builder.create();
      service.getCommandLine();

      Application.create(ServerDriver.class)
           .path("..")
           .file("ternd")
           .file("tern-studio")
           .register(StudioApplication.class)
           .create(list)
           .name("Apache/2.2.14")
           .session(SESSION_ID)
           .threads(10)
           .log(logger)
           .start();
   }


   @Slf4j
   private static class StudioAccessLogger implements org.simpleframework.resource.container.Logger {

      private final BlockingQueue<String> messages;
      private final ThreadBuilder builder;
      private final AtomicBoolean active;
      private final LogFlusher flusher;

      public StudioAccessLogger() {
         this.builder = new ThreadBuilder(true);
         this.active = new AtomicBoolean(false);
         this.messages = new LinkedBlockingQueue<>();
         this.flusher = new LogFlusher();
      }

      @Override
      public void log(Request request) {
         if(active.compareAndSet(false, true)) {
            Thread thread = builder.newThread(flusher, LogFlusher.class);
            thread.start();
         }
         String message = format(request);
         messages.offer(message);
      }

      private String format(Request request) {
         String agent = request.getValue(Protocol.USER_AGENT);
         String host = request.getValue(Protocol.HOST);
         ContentType type = request.getContentType();
         String method = request.getMethod();
         String path = request.getTarget();

         return String.format("%s %s://%s%s '%s' %s %s",
              method, request.isSecure() ? "https" : "http", host, path,
              agent, type != null ? type : "",
              type != null ? request.getContentLength() : "",
              request.getClientAddress().getAddress());
      }

      private class LogFlusher implements Runnable {

         private final List<String> batch;

         public LogFlusher() {
            this.batch = new ArrayList<>();
         }

         @Override
         public void run() {
            try {
               while (active.get()) {
                  String message = messages.take();

                  if(message != null) {
                     log.info(message);
                     batch.clear(); // play it safe and clear before also
                     messages.drainTo(batch, 100);
                     batch.forEach(log::info);
                     batch.clear();
                  }
               }
            } catch(Throwable cause) {
               log.error("Log flusher failed", cause);
            } finally {
               active.set(false);
            }
         }
      }
   }
}
