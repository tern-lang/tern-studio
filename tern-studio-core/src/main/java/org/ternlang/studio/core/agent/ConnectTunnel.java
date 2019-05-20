package org.ternlang.studio.core.agent;

import org.simpleframework.http.Request;
import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;
import org.ternlang.service.resource.annotation.CONNECT;
import org.ternlang.service.resource.annotation.HeaderParam;
import org.ternlang.service.resource.annotation.Path;
import org.ternlang.service.resource.annotation.PathParam;
import org.ternlang.studio.core.agent.worker.WorkerProcessSubscriber;

import lombok.AllArgsConstructor;

@Path(".*:\\d+")
@AllArgsConstructor
public class ConnectTunnel {
   
   private static final String TUNNEL_RESPONSE = "HTTP/1.1 200 OK\r\n" +
         "Content-Length: 0\r\n" +
         "Connection: keep-alive\r\n"+
         "Date: %s\r\n" +
         "Server: Server/1.0\r\n" +
         "\r\n";
   
   private final WorkerProcessSubscriber subscriber; // used when an event executes itsel

   @CONNECT
   @Path("/connect/{agent}")
   public void connect(
         @PathParam("agent") String source,  // incorrect
         @HeaderParam("date") String date, // incorrect
         Request request) throws Throwable 
   {
      Channel channel = request.getChannel();
      ByteWriter writer = channel.getWriter();
      String header = String.format(TUNNEL_RESPONSE, date);
      byte[] data = header.getBytes("UTF-8");         

      writer.write(data);
      writer.flush();
      subscriber.subscribe(channel, source); // establish the connection
   }

}