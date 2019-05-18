package org.ternlang.studio.service.agent;

import static org.simpleframework.http.Method.CONNECT;
import static org.simpleframework.http.Status.METHOD_NOT_ALLOWED;

import java.io.IOException;
import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;
import org.ternlang.studio.agent.event.ProcessEventListener;
import org.ternlang.studio.resource.Resource;
import org.ternlang.studio.resource.ResourcePath;
import org.ternlang.studio.service.ProcessManager;
import org.ternlang.studio.service.agent.worker.WorkerProcessBeginListener;
import org.springframework.stereotype.Component;

@Component
@ResourcePath(".*:\\d+/connect/.+")
public class ConnectTunnelResource implements Resource {
   
   private static final String CONTENT_TYPE = "text/plain";
   private static final String TUNNEL_RESPONSE = "HTTP/1.1 200 OK\r\n" +
         "Content-Length: 0\r\n" +
         "Connection: keep-alive\r\n"+
         "Date: %s\r\n" +
         "Server: Server/1.0\r\n" +
         "\r\n";
   
   private final ProcessEventListener listener; // used when an event executes itself
   private final ProcessManager manager;
   
   public ConnectTunnelResource(ProcessManager manager) throws IOException {
      this.listener = new WorkerProcessBeginListener(manager);
      this.manager = manager;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      String method = request.getMethod();
      Channel channel = request.getChannel();
      
      if(method.equalsIgnoreCase(CONNECT)) {
         Path path = request.getPath();
         String[] segments = path.getSegments();
         String source = segments[1];
         ByteWriter writer = channel.getWriter();
         String date = request.getValue(Protocol.DATE);
         String header = String.format(TUNNEL_RESPONSE, date);
         byte[] data = header.getBytes("UTF-8");         

         writer.write(data);
         writer.flush();
         manager.connect(listener, channel, source); // establish the connection
      } else {
         PrintStream stream = response.getPrintStream();
         
         response.setStatus(METHOD_NOT_ALLOWED);
         response.setContentType(CONTENT_TYPE);
         stream.println("Method not allowed"); // error message
         stream.close();
      }
   }

}