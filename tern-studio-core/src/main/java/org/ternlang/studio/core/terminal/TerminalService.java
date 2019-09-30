package org.ternlang.studio.core.terminal;

import java.io.File;
import java.util.Map;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.socket.Frame;
import org.simpleframework.http.socket.FrameChannel;
import org.simpleframework.http.socket.FrameListener;
import org.simpleframework.http.socket.FrameType;
import org.simpleframework.http.socket.Reason;
import org.simpleframework.http.socket.Session;
import org.simpleframework.http.socket.service.Service;
import org.simpleframework.module.annotation.Component;
import org.simpleframework.resource.annotation.Subscribe;
import org.ternlang.studio.project.Project;
import org.ternlang.studio.project.Workspace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Subscribe("/session/.*")
public class TerminalService implements Service {

   private final Workspace workspace;
   private final ObjectMapper mapper;
   
   public TerminalService(Workspace workspace) {
      this.mapper = new ObjectMapper();
      this.workspace = workspace;
   }
   
   @Override
   public void connect(Session session) {
      FrameChannel channel = session.getChannel();
      Request request = session.getRequest();
      Path path = request.getPath();
      Project project = workspace.createProject(path);
      String[] segments = path.getSegments();
      File root = project.getBasePath();
      String suffix = "/";
      
      if(segments.length > 2) {
         suffix = path.getPath(2);
      }
      try {
         File directory = new File(root, suffix);
         SessionController client = new SessionController(channel, mapper, directory);
         
         channel.register(client);
      } catch(Exception e) {
         log.warn("Error creating session", e);
      }
   }

   private static class SessionController implements FrameListener {

      private final TypeReference<Map<String, String>> reference;
      private final TerminalProcess process;
      private final ObjectMapper mapper;

      public SessionController(FrameChannel channel, ObjectMapper mapper, File directory) {
         this.reference = new TypeReference<Map<String, String>>() {};
         this.process = new TerminalProcess(channel, mapper, directory);
         this.mapper = mapper;
      }

      @Override
      public void onFrame(Session session, Frame frame) {
         FrameType type = frame.getType();

         if (type.isText()) {
            try {
               String text = frame.getText();
               Map<String, String> message = mapper.readValue(text, reference);
               String value = message.get("type");

               log.info("Terminal command: {}", text);
               
               if ("TERMINAL_INIT".equals(value)) {
                  process.onTerminalInit();
               } else if ("TERMINAL_READY".equals(value)) {
                  process.onTerminalReady();
               } else if ("TERMINAL_COMMAND".equals(value)) {
                  String command = message.get("command");
                  process.onTerminalCommand(command);
               } else if ("TERMINAL_RESIZE".equals(value)) {
                  String columns = message.get("columns");
                  String rows = message.get("rows");
                  process.onTerminalResize(columns, rows);
               }

            } catch (Exception e) {
               log.info("Could not process frame", e);
            }
         }
      }

      @Override
      public void onError(Session session, Exception cause) {
         log.info("Error occured", cause);
         process.onTerminalClose();
      }

      @Override
      public void onClose(Session session, Reason reason) {
         log.info("Session closed");
         process.onTerminalClose();
      }
   }
}
