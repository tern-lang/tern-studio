package org.ternlang.studio.core.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.http.socket.FrameChannel;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TerminalConsole implements Runnable {
   
   private final BufferedReader reader;
   private final FrameChannel channel;
   private final ObjectMapper mapper;
   
   public void start() {
      Thread thread = new Thread(this);
      thread.start();
   }

   @Override
   public void run() {
      try {
         int count;
         char[] data = new char[1 * 1024];

         while ((count = reader.read(data, 0, data.length)) != -1) {
             StringBuilder builder = new StringBuilder(count);
             builder.append(data, 0, count);
             print(builder.toString());
         }

     } catch (Exception e) {
         e.printStackTrace();
     }
   }
   
   public void print(String text) throws IOException {

      Map<String, String> map = new HashMap<>();
      map.put("type", "TERMINAL_PRINT");
      map.put("text", text);

      String message = mapper.writeValueAsString(map);

      channel.send(message);

  }

}
