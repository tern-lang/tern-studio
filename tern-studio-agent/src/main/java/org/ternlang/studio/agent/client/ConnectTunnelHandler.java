package org.ternlang.studio.agent.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.ternlang.studio.agent.log.TraceLogger;

public class ConnectTunnelHandler {

   private static final int[] TERMINAL = {'\r', '\n', '\r', '\n'};
   private static final String CHARSET = "UTF-8";
   private static final String FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
   private static final String TIME_ZONE = "GMT";
   private static final String REQUEST = 
   "CONNECT %s:%s/connect/%s HTTP/1.1\r\n" +
   "Accept: */*\r\n"+   
   "Host: %s:%s\r\n" +
   "Date: %s\r\n"+
   "\r\n";

   private final ByteArrayOutputStream buffer;
   private final TraceLogger logger;
   private final DateFormat format;
   private final TimeZone zone;
   private final String process;
   private final int connect;
   
   public ConnectTunnelHandler(TraceLogger logger, String process, int connect) {
      this.buffer = new ByteArrayOutputStream();
      this.format = new SimpleDateFormat(FORMAT);
      this.zone = TimeZone.getTimeZone(TIME_ZONE);
      this.process = process;
      this.logger = logger;
      this.connect = connect;
   }
   
   public synchronized void tunnel(Socket socket) throws Exception{
      format.setTimeZone(zone);
      send(socket);
      receive(socket);
   }
   
   private synchronized void send(Socket socket) throws Exception {
      OutputStream output = socket.getOutputStream();
      InetAddress address = socket.getInetAddress();
      String host = address.getHostAddress();
      int port = socket.getPort();
      long time = System.currentTimeMillis();
      String date = format.format(time);
      String request = String.format(REQUEST, host, connect, process, host, port, date);
      byte[] header = request.getBytes(CHARSET);
      
      output.write(header);
      output.flush();
   }
   
   private synchronized void receive(Socket socket) throws Exception {
      InputStream input = socket.getInputStream();
      int seek = 0;

      while(true) {
         int count = input.read();
         
         if(count != TERMINAL[seek++]) {
            seek = 0;
         }
         buffer.write(count);
         
         if(seek == TERMINAL.length) {
            break;
         }
      }
      String header = buffer.toString("UTF-8");

      buffer.reset();
      logger.trace(header);
   }
}