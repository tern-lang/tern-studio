package org.ternlang.studio.service.agent.local;

import java.net.InetAddress;
import java.net.Socket;

import lombok.SneakyThrows;

import org.ternlang.studio.agent.local.LocalMessageProducer;
import org.ternlang.studio.agent.local.message.AttachRequest;
import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachRequest;
import org.ternlang.studio.agent.local.message.DetachResponse;
import org.ternlang.studio.project.config.ProcessConfiguration;
import org.springframework.stereotype.Component;

@Component
public class LocalProcessClient {
   
   private final ProcessConfiguration configuration;
   private final LocalMessageProducer producer; 
   
   public LocalProcessClient(ProcessConfiguration configuration) {
      this.producer = new LocalMessageProducer();
      this.configuration = configuration;
   }
   
   @SneakyThrows
   public AttachResponse attach(String projectName, String remoteHost, int remotePort){
      String localHost = InetAddress.getLocalHost().getCanonicalHostName();
      int localPort = configuration.getPort();
      
      try {
         AttachRequest request = new AttachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         try {
            return producer.attach(socket, request);
         }finally {
            socket.close();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not attach to " + remoteHost + ":" + remotePort, e);
      }  
   }
   
   @SneakyThrows
   public DetachResponse detach(String projectName, String remoteHost, int remotePort){
      String localHost = InetAddress.getLocalHost().getCanonicalHostName();
      int localPort = configuration.getPort();
      
      try {
         DetachRequest request = new DetachRequest(projectName, localHost, localPort);
         Socket socket = new Socket(remoteHost, remotePort);
         
         try {
            return producer.detach(socket, request);
         }finally {
            socket.close();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not detach to " + remoteHost + ":" + remotePort, e);
      }  
   }
}
