package org.ternlang.studio.agent.local;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.ternlang.studio.agent.local.message.AttachRequest;
import org.ternlang.studio.agent.local.message.AttachResponse;
import org.ternlang.studio.agent.local.message.DetachRequest;
import org.ternlang.studio.agent.local.message.DetachResponse;

public class LocalMessageProducer {
   
   public LocalMessageProducer(){
      super();
   }
   
   public AttachResponse attach(Socket socket, AttachRequest request) throws Exception {
      OutputStream stream = socket.getOutputStream();
      InputStream in = socket.getInputStream();
      ObjectOutputStream output = new ObjectOutputStream(stream);   
      ObjectInputStream input = new ObjectInputStream(in);
      
      output.writeObject(request);
      return (AttachResponse)input.readObject();
   }
   
   public DetachResponse detach(Socket socket, DetachRequest request) throws Exception {
      OutputStream out = socket.getOutputStream();
      InputStream in = socket.getInputStream();
      ObjectOutputStream output = new ObjectOutputStream(out);   
      ObjectInputStream input = new ObjectInputStream(in);   
      
      output.writeObject(request);
      return (DetachResponse)input.readObject();
   }
}
